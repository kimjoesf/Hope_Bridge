package com.hopebridge.model.dto;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Everything a donor filled in on the donate form, carried from the controller into the model.
 * Fields that do not apply to the chosen method are simply null.
 */
public record DonationRequest(
        int donorId,
        Integer campaignId,
        Integer categoryId,
        String methodCode,
        BigDecimal amount,
        String checkNumber,
        String itemDescription,
        BigDecimal estimatedValue,
        Set<String> addOns,
        String employerName,
        String honoreeName) {

    // this is just because when add-ons is null it throws an error NullPointerException .        
    public DonationRequest {
        addOns = addOns == null ? Set.of() : Set.copyOf(addOns);
    }
}