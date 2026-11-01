package com.hopebridge.model.factory;

import com.hopebridge.model.strategy.donation.CheckMethod;
import com.hopebridge.model.strategy.donation.DonationMethod;
import com.hopebridge.model.strategy.donation.InKindMethod;
import com.hopebridge.model.strategy.donation.OnlineMethod;

import java.util.List;

/**
 * FACTORY - turns the method the donor clicked ("ONLINE", "CHECK", "IN_KIND")
 * into the matching donation strategy object.
 */
public final class DonationMethodFactory {

    private static final TypeRegistry<DonationMethod> METHODS = new TypeRegistry<DonationMethod>("donation method") 
            .register("ONLINE", OnlineMethod::new)
            .register("CHECK", CheckMethod::new)
            .register("IN_KIND", InKindMethod::new);

    private DonationMethodFactory() {
    }

    public static DonationMethod create(String code) {
        return METHODS.create(code);
    }

    // The donate page builds its method tabs from this, so a newly registered method shows up by itself.
    public static List<DonationMethod> availableMethods() {
        return METHODS.createAll();
    }
}
