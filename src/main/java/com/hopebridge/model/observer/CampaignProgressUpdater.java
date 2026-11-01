package com.hopebridge.model.observer;

import com.hopebridge.model.dao.CampaignDao;
import com.hopebridge.model.entity.Donation;

/**
 * OBSERVER - a Concrete Observer.
 * Client requirement: campaign goal progress. When a gift is made towards a campaign,
 * its "raised so far" moves forward and the progress bars update everywhere.
 */
public class CampaignProgressUpdater implements Observer<Donation> {

    private final CampaignDao campaignDao;

    public CampaignProgressUpdater(Subject<Donation> donationTracker, CampaignDao campaignDao) {
        this.campaignDao = campaignDao;
        donationTracker.registerObserver(this);
    }

    @Override
    public void update(Donation donation) {
        if (donation.getCampaignId() != null) {
            campaignDao.addToRaised(donation.getCampaignId(), donation.getTotalAmount());
        }
    }
}
