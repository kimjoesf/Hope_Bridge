package com.hopebridge.model.observer;

import com.hopebridge.model.entity.Donation;
import com.hopebridge.model.service.NotificationService;
import com.hopebridge.model.support.Money;

/**
 * OBSERVER - a Concrete Observer.
 * Thanks the donor straight away, through whichever channel they prefer
 * (the Strategy pattern takes care of the format).
 */
public class ThankYouNotifier implements Observer<Donation> {

    private final NotificationService notificationService;

    public ThankYouNotifier(Subject<Donation> donationTracker, NotificationService notificationService) {
        this.notificationService = notificationService;
        donationTracker.registerObserver(this);
    }

    @Override
    public void update(Donation donation) {
        notificationService.send(donation.getDonorId(), "Thank you for your gift!",
                "We received your " + donation.getDescription().toLowerCase() + " worth " + Money.format(donation.getTotalAmount())
                        + ". Your receipt number is " + donation.getReceiptNumber()
                        + " and your reference is " + donation.getReference() + ". Thank you for standing with us.");
    }
}
