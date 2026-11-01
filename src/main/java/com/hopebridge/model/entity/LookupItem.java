package com.hopebridge.model.entity;

/**
 * One row of any lookup table (a skill, a donation category, an event category...).
 * They all share this shape, which is what makes a single CRUD screen possible.
 */
public class LookupItem {

    private int id;
    private String name;
    private String description;
    private boolean active = true;

    public LookupItem() {
    }

    public LookupItem(int id, String name, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public boolean isNew() {
        return id == 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
