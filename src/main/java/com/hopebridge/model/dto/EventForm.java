package com.hopebridge.model.dto;

import java.time.LocalDateTime;

/**
 * What staff typed on the create / edit event form. A null id means a brand-new event.
 */
public record EventForm(
        Integer id,
        String title,
        String summary,
        Integer categoryId,
        String location,
        LocalDateTime startsAt,
        Integer capacity) {

    public boolean isNew() {
        return id == null;
    }
}
