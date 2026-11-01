package com.hopebridge.model.observer;

import com.hopebridge.model.dao.DonationDao;
import com.hopebridge.model.dao.UserDao;
import com.hopebridge.model.entity.Donation;
import com.hopebridge.model.entity.DonorTier;
import com.hopebridge.model.service.NotificationService;

/**
 * OBSERVER - a Concrete Observer.
 * Client requirement: donor recognition tiers. After each gift we recheck the donor's
 * lifetime giving, and if they climbed a rung we save it and congratulate them.
 */
public class DonorTierUpdater implements Observer<Donation> {

    private final UserDao userDao;
    private final DonationDao donationDao;
    private final NotificationService notificationService;

    public DonorTierUpdater(Subject<Donation> donationTracker, UserDao userDao, DonationDao donationDao,
                            NotificationService notificationService) {
        this.userDao = userDao;
        this.donationDao = donationDao;
        this.notificationService = notificationService;
        donationTracker.registerObserver(this);
    }

    @Override
    public void update(Donation donation) {
        userDao.findById(donation.getDonorId()).ifPresent(donor -> {
            DonorTier reached = DonorTier.forLifetimeGiving(donationDao.lifetimeGiving(donor.getId()));
            if (reached.isHigherThan(donor.getDonorTier())) {
                userDao.updateTier(donor.getId(), reached);
                donor.setDonorTier(reached);
                notificationService.send(donor, "You are now a " + reached.getLabel() + " supporter!",
                        "Your generosity just moved you up to our " + reached.getLabel()
                                + " tier. Thank you for being such a big part of our work.");
            }
        });
    }
}
