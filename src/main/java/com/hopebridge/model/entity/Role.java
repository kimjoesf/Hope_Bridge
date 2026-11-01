package com.hopebridge.model.entity;

/**
 * The kinds of people who use HopeBridge.
 */
public enum Role {

    DONOR("Donor", true),
    VOLUNTEER("Volunteer", true),
    STAFF("Staff", false);

    private final String label;
    private final boolean selfRegistrable;

    Role(String label, boolean selfRegistrable) {
        this.label = label;
        this.selfRegistrable = selfRegistrable;
    }

    public String getLabel() {
        return label;
    }

    // Anyone can sign up as a donor or a volunteer, but staff accounts are created by the organization.
    public boolean isSelfRegistrable() {
        return selfRegistrable;
    }
}
