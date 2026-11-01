package com.hopebridge.model.observer;

import com.hopebridge.model.entity.Donation;

/**
 * OBSERVER - a Concrete Subject (our "WeatherData").
 *
 * "Notify interested parties when a new donation is received."
 * The tracker does not know who is listening or what they do with the news:
 * one observer issues the receipt, another moves the campaign bar, another
 * thanks the donor... We can add a new one any time without touching this class.
 */
public class DonationTracker extends AbstractSubject<Donation> {

    private Donation latestDonation;

    // Like setMeasurements() + measurementsChanged(): store the new state, then notify.
    // synchronized keeps two donations arriving at the same moment from overwriting each other's state.
    public synchronized void donationReceived(Donation donation) {
        this.latestDonation = donation;
        notifyObservers();
    }

    @Override
    protected Donation getState() {
        return latestDonation;
    }
}
