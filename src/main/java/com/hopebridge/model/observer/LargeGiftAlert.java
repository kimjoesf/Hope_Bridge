package com.hopebridge.model.observer;

import com.hopebridge.model.entity.Donation;
import com.hopebridge.model.entity.Role;
import com.hopebridge.model.service.NotificationService;
import com.hopebridge.model.support.Money;

import java.math.BigDecimal;

/**
 * OBSERVER - a Concrete Observer.
 * Lets the staff know when a large gift arrives, so someone can reach out personally.
 * It was added without touching DonationTracker at all - the power of loose coupling.
 */
public class LargeGiftAlert implements Observer<Donation> {

    private static final BigDecimal THRESHOLD = new BigDecimal("1000");

    private final NotificationService notificationService;

    public LargeGiftAlert(Subject<Donation> donationTracker, NotificationService notificationService) {
        this.notificationService = notificationService;
        donationTracker.registerObserver(this);
    }

    @Override
    public void update(Donation donation) {
        if (donation.getTotalAmount().compareTo(THRESHOLD) >= 0) {
            notificationService.sendToRole(Role.STAFF, "Large gift received",
                    "A gift worth " + Money.format(donation.getTotalAmount()) + " just arrived (receipt "
                            + donation.getReceiptNumber() + "). It might be a good moment for a personal thank-you call.");
        }
    }
}
