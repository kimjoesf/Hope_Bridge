package com.hopebridge.model.observer;

import com.hopebridge.model.dao.DonationDao;
import com.hopebridge.model.entity.Donation;

/**
 * OBSERVER - a Concrete Observer.
 * "Generate donation receipts": every new donation gets an official receipt number.
 *
 * It is registered first, so the observers after it can already mention the receipt number.
 */
public class ReceiptIssuer implements Observer<Donation> {

    private final DonationDao donationDao;

    // The constructor is passed the subject, and we use it to register ourselves as an observer.
    public ReceiptIssuer(Subject<Donation> donationTracker, DonationDao donationDao) {
        this.donationDao = donationDao;
        donationTracker.registerObserver(this);
    }

    @Override
    public void update(Donation donation) {
        String receiptNumber = "HB-" + donation.getCreatedAt().getYear() + "-" + String.format("%06d", donation.getId());
        donationDao.updateReceiptNumber(donation.getId(), receiptNumber);
        donation.setReceiptNumber(receiptNumber);
    }
}
