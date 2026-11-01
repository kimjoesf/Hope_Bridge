package com.hopebridge.model.entity;

import java.util.Arrays;
import java.util.Optional;

/**
 * Every lookup table the organization can manage.
 *
 * Adding a new lookup table is one line here plus one CREATE TABLE in the schema;
 * the DAO, the controller and the pages pick it up automatically.
 */
public enum LookupTable {

    DONATION_CATEGORIES("donation-categories", "donation_categories", "Donation Categories", "Category",
            "The causes a gift can be directed to."),
    SKILLS("skills", "skills", "Volunteer Skills", "Skill",
            "What volunteers can bring, used to match them with the right work."),
    EVENT_CATEGORIES("event-categories", "event_categories", "Event Categories", "Event category",
            "How events are grouped: fundraisers, workshops, outreach and more."),
    AVAILABILITY_SLOTS("availability-slots", "availability_slots", "Availability Slots", "Slot",
            "The time windows volunteers can choose from.");   // this is simply the available time slots for volunteer to choose from like for instance morning, evening, etc.

    private final String slug;
    private final String tableName;
    private final String title;
    private final String itemLabel;
    private final String purpose;

    LookupTable(String slug, String tableName, String title, String itemLabel, String purpose) {
        this.slug = slug;
        this.tableName = tableName;
        this.title = title;
        this.itemLabel = itemLabel;
        this.purpose = purpose;
    }
       // slug is a URL-friendly identifier for the table, it is simply a text that can be embedded in the URL for search .
      // The slug comes from the URL, so we only ever accept one of the known values, same what happens in the web development.
     // That also means the table name we put into SQL can never come from user input.
    // this method has optional return type because it is possible that the user enters a wrong slug or a non-existent slug so to avoid throwing an exception, we return an optional.
    public static Optional<LookupTable> fromSlug(String slug) {
        return Arrays.stream(values()).filter(table -> table.slug.equals(slug)).findFirst();
    }

    public String getSlug() {
        return slug;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTitle() {
        return title;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public String getPurpose() {
        return purpose;
    }
}
