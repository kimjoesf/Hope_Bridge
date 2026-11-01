package com.hopebridge.model.factory;

import com.hopebridge.model.decorator.Contribution;
import com.hopebridge.model.decorator.CoverFeesAddOn;
import com.hopebridge.model.decorator.DedicationAddOn;
import com.hopebridge.model.decorator.EmployerMatchAddOn;
import com.hopebridge.model.dto.DonationRequest;
import com.hopebridge.model.support.Rules;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * FACTORY - wraps a contribution in the add-on decorators the donor ticked.
 *
 * The donate form only sends codes like "COVER_FEES". This class knows which
 * decorator each code means and does the "contribution = new X(contribution)"
 * wrapping from the lab, so the service never has to.
 *
 * Order matters with decorators: the map's insertion order is the wrapping order.
 * Fees are covered first; since the fee never reaches the organization, the employer
 * matches the gift itself, not the fee.
 */
public final class AddOnFactory {

    public static final String COVER_FEES = "COVER_FEES";
    public static final String EMPLOYER_MATCH = "EMPLOYER_MATCH";
    public static final String DEDICATION = "DEDICATION";

    private static final Map<String, AddOnOption> OPTIONS = new LinkedHashMap<>();

    static {
        register(new AddOnOption(COVER_FEES, "Cover processing fees",
                "Add 3% so 100% of your gift reaches the cause.",
                (contribution, request) -> new CoverFeesAddOn(contribution)));
        register(new AddOnOption(EMPLOYER_MATCH, "Employer match",
                "Your employer doubles your gift.",
                (contribution, request) -> new EmployerMatchAddOn(contribution,
                        Rules.requireText(request.employerName(), "Employer name", 80))));
        register(new AddOnOption(DEDICATION, "Dedicate this gift",
                "Give in honour of someone special.",
                (contribution, request) -> new DedicationAddOn(contribution,
                        Rules.requireText(request.honoreeName(), "Honoree name", 80))));
    }

    private AddOnFactory() {
    }

    private static void register(AddOnOption option) {
        OPTIONS.put(option.getCode(), option);
    }

    public static Contribution decorate(Contribution contribution, DonationRequest request) {
        Rules.require(OPTIONS.keySet().containsAll(request.addOns()), "One of the selected add-ons is not available.");
        Contribution decorated = contribution;
        for (AddOnOption option : OPTIONS.values()) {
            if (request.addOns().contains(option.getCode())) {
                decorated = option.wrap(decorated, request);
            }
        }
        return decorated;
    }

    public static Collection<AddOnOption> availableAddOns() {
        return OPTIONS.values();
    }

    /**
     * One choice on the donate form: what it is called, what it does, and which decorator it applies.
     */
    public static final class AddOnOption {

        private final String code;
        private final String label;
        private final String description;
        private final BiFunction<Contribution, DonationRequest, Contribution> wrapper;

        private AddOnOption(String code, String label, String description,
                            BiFunction<Contribution, DonationRequest, Contribution> wrapper) {
            this.code = code;
            this.label = label;
            this.description = description;
            this.wrapper = wrapper;
        }

        private Contribution wrap(Contribution contribution, DonationRequest request) {
            return wrapper.apply(contribution, request);
        }

        public String getCode() {
            return code;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }
    }
}
