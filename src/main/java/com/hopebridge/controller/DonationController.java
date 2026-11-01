package com.hopebridge.controller;

import com.hopebridge.model.decorator.CoverFeesAddOn;
import com.hopebridge.model.decorator.EmployerMatchAddOn;
import com.hopebridge.model.dto.DonationRequest;
import com.hopebridge.model.entity.LookupTable;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.service.DonationService;
import com.hopebridge.model.service.DonationService.DonationResult;
import com.hopebridge.model.service.LookupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * CONTROLLER - giving: the donate form, the donor's history and printable receipts.
 */
public class DonationController extends BaseController {

    private static final List<Integer> AMOUNT_PRESETS = List.of(25, 50, 100, 250);

    private final DonationService donationService;
    private final LookupService lookupService;

    public DonationController(ControllerSupport support, DonationService donationService, LookupService lookupService) {
        super(support);
        this.donationService = donationService;
        this.lookupService = lookupService;
        get("/donate", this::showForm);
        post("/donate", this::donate);
        get("/panel/donations", this::history);
        get("/panel/donations/receipt", this::receipt);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireUser(request);
        render(request, response, "donate", model(
                "campaigns", donationService.activeCampaigns(),
                "categories", lookupService.active(LookupTable.DONATION_CATEGORIES),
                "methods", donationService.availableMethods(),
                "addOns", donationService.availableAddOns(),
                "presets", AMOUNT_PRESETS,
                "selectedCampaign", RequestParams.integer(request, "campaign"),
                // The live total on the page previews the same numbers the decorators use.
                "feeRate", CoverFeesAddOn.FEE_RATE,
                "matchMultiplier", EmployerMatchAddOn.MATCH_MULTIPLIER));
    }

    // Turn the form into a DonationRequest and let the model do the rest.
    private void donate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User donor = requireUser(request);
        DonationRequest donationRequest = new DonationRequest(
                donor.getId(),
                RequestParams.integer(request, "campaignId"),
                RequestParams.integer(request, "categoryId"),
                RequestParams.text(request, "method"),
                RequestParams.decimal(request, "amount"),
                RequestParams.text(request, "checkNumber"),
                RequestParams.text(request, "itemDescription"),
                RequestParams.decimal(request, "estimatedValue"),
                RequestParams.values(request, "addOns"),
                RequestParams.text(request, "employerName"),
                RequestParams.text(request, "honoreeName"));

        DonationResult result = donationService.donate(donationRequest);
        success(request, "Thank you, " + donor.getFirstName() + "! " + result.message());
        redirect(response, "/panel/donations/receipt?id=" + result.donation().getId());
    }

    private void history(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireUser(request);
        render(request, response, "panel/donations", model(
                "donations", donationService.historyFor(user.getId()),
                "summary", donationService.summaryFor(user)));
    }

    private void receipt(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireUser(request);
        render(request, response, "panel/receipt", model(
                "donation", donationService.receiptFor(requiredId(request, "id"), user)));
    }
}
