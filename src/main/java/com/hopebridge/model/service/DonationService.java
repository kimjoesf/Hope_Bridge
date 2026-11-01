package com.hopebridge.model.service;

import com.hopebridge.model.dao.CampaignDao;
import com.hopebridge.model.dao.DonationDao;
import com.hopebridge.model.decorator.Contribution;
import com.hopebridge.model.dto.DonationRequest;
import com.hopebridge.model.dto.DonorSummary;
import com.hopebridge.model.entity.Campaign;
import com.hopebridge.model.entity.Donation;
import com.hopebridge.model.entity.DonorTier;
import com.hopebridge.model.entity.LookupTable;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.exception.NotFoundException;
import com.hopebridge.model.exception.ValidationException;
import com.hopebridge.model.factory.AddOnFactory;
import com.hopebridge.model.factory.DonationMethodFactory;
import com.hopebridge.model.observer.DonationTracker;
import com.hopebridge.model.strategy.donation.DonationMethod;
import com.hopebridge.model.strategy.donation.DonationOutcome;
import com.hopebridge.model.strategy.donation.DonationProcessor;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * Donation management - and the place where four patterns meet:
 *
 *   Factory    picks the donation method object from the code the donor clicked,
 *   Strategy   processes the gift the way that method requires,
 *   Decorator  wraps the gift with the add-ons the donor chose,
 *   Observer   tells everyone interested that a new donation arrived.
 */
public class DonationService {

    private final DonationDao donationDao;
    private final CampaignDao campaignDao;
    private final LookupService lookupService;
    private final DonationTracker donationTracker;

    public DonationService(DonationDao donationDao, CampaignDao campaignDao,
                           LookupService lookupService, DonationTracker donationTracker) {
        this.donationDao = donationDao;
        this.campaignDao = campaignDao;
        this.lookupService = lookupService;
        this.donationTracker = donationTracker;
    }

    public DonationResult donate(DonationRequest request) {
        Campaign campaign = request.campaignId() == null ? null : activeCampaign(request.campaignId());
        // A campaign already belongs to a cause, so it decides the category; otherwise the donor's pick does.
        Integer categoryId = campaign != null ? Integer.valueOf(campaign.getCategoryId()) : request.categoryId();
        // just to ensure the selected category is active .
        lookupService.requireActive(LookupTable.DONATION_CATEGORIES, categoryId, "Please choose a cause for your gift.");

        // Strategy: the processor (context) delegates to whichever method the factory built, the factory just make the instatiation okay while strategy have the implementation.
        // so simply the donation processor just process or execute the code already written in the strategy, but the strategy that get executed is what the factory created based on the user selection .
        DonationProcessor processor = new DonationProcessor(DonationMethodFactory.create(request.methodCode()));  
        DonationOutcome outcome = processor.process(request);

        // Decorator: start from the plain gift, then let the factory wrap it in each chosen add-on.
        Contribution base = processor.contributionFor(request);
        Contribution contribution = AddOnFactory.decorate(base, request);

        Donation donation = new Donation();
        donation.setDonorId(request.donorId());
        donation.setCampaignId(campaign == null ? null : campaign.getId());
        donation.setCategoryId(categoryId);
        donation.setMethod(processor.getDonationMethod().getCode());
        donation.setBaseAmount(base.amount());
        donation.setTotalAmount(contribution.amount());
        donation.setTaxDeductibleAmount(contribution.taxDeductibleAmount());
        donation.setDescription(contribution.getDescription());
        donation.setAddOns(request.addOns().isEmpty() ? null : String.join(",", new TreeSet<>(request.addOns())));
        donation.setItemDescription(trimToNull(request.itemDescription()));
        donation.setCheckNumber(trimToNull(request.checkNumber()));
        donation.setStatus(outcome.status());
        donation.setReference(outcome.reference());
        donationDao.insert(donation);

        // Observer: one call, and every registered observer reacts in its own way.
        donationTracker.donationReceived(donation);
        return new DonationResult(donation, outcome.message());
    }

    public List<Donation> historyFor(int donorId) {
        return donationDao.findByDonor(donorId);
    }

    public List<Donation> recent(int limit) {
        return donationDao.findRecent(limit);
    }

    // Donors can only open their own receipts; staff can open any of them.
    public Donation receiptFor(int donationId, User viewer) {
        return donationDao.findById(donationId)
                .filter(donation -> donation.getDonorId() == viewer.getId() || viewer.isStaff())
                .orElseThrow(() -> new NotFoundException("Receipt not found."));
    }

    public DonorSummary summaryFor(User donor) {
        BigDecimal lifetime = donationDao.lifetimeGiving(donor.getId());
        return new DonorSummary(lifetime, donationDao.countByDonor(donor.getId()), DonorTier.forLifetimeGiving(lifetime));
    }

    public List<Campaign> activeCampaigns() {
        return campaignDao.findActive();
    }

    public List<DonationMethod> availableMethods() {
        return DonationMethodFactory.availableMethods();
    }

    public Collection<AddOnFactory.AddOnOption> availableAddOns() {
        return AddOnFactory.availableAddOns();
    }

    private Campaign activeCampaign(int campaignId) {
        return campaignDao.findById(campaignId)
                .filter(Campaign::isActive)
                .orElseThrow(() -> new ValidationException("That campaign is no longer accepting gifts."));
    }

    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    /**
     * The saved donation plus the message its donation method wants the donor to read.
     */
    public record DonationResult(Donation donation, String message) {
    }
}
