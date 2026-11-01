package com.hopebridge.model.entity;

import com.hopebridge.model.strategy.communication.CommunicationStrategy;

import java.time.LocalDateTime;

/**
 * STRATEGY - the Context (think of the abstract Duck class in the lab).
 *
 * Every user can be contacted, but HOW we contact them varies: some prefer
 * e-mail, some SMS, some a social-media message. If "contact()" lived here as
 * one big if/else, every new channel would force us to edit this class.
 *
 * So we separate what changes (the channel) from what stays the same (the user).
 * The user just HAS-A CommunicationStrategy and delegates to it, exactly like
 * Duck has-a FlyBehavior and performFly() calls flyBehavior.fly().
 */
public abstract class User {

    private int id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private DonorTier donorTier = DonorTier.SUPPORTER;
    private LocalDateTime createdAt;

    // The behavior chosen is each subclass would define its own default strategy in its constructor.
    protected CommunicationStrategy communicationStrategy;

    public abstract Role getRole();

    // to perform sending the message using the current communication strategy channel .
    public Notification performContact(String subject, String body) {
        return communicationStrategy.deliver(this, subject, body);
    }

    // The setter is what lets us change the behavior at runtime (the user picks a new channel from the panel).
    public void setCommunicationStrategy(CommunicationStrategy communicationStrategy) {
        this.communicationStrategy = communicationStrategy;
    }

    public CommunicationStrategy getCommunicationStrategy() {
        return communicationStrategy;
    }

    public String getFirstName() {
        return fullName == null ? "" : fullName.trim().split("\\s+")[0];
    }

    public boolean isStaff() {
        return getRole() == Role.STAFF;
    }

    public boolean isVolunteer() {
        return getRole() == Role.VOLUNTEER;
    }

    public boolean isDonor() {
        return getRole() == Role.DONOR;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public DonorTier getDonorTier() {
        return donorTier;
    }

    public void setDonorTier(DonorTier donorTier) {
        this.donorTier = donorTier;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
