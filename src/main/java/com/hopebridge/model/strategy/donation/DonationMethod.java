package com.hopebridge.model.strategy.donation;

import com.hopebridge.model.decorator.Contribution;
import com.hopebridge.model.dto.DonationRequest;

/**
 * STRATEGY - the Strategy interface (compare with PaymentStrategy in the lab).
 *
 * "Implement a flexible donation system to accommodate various donation methods."
 * Online gifts, checks and in-kind goods are each received in a different way,
 * so each way gets its own class. Supporting a new method later means one more
 * class - the processor and the service do not change.
 */
public interface DonationMethod {

    String getCode();

    String getLabel();

    String getDescription();

    // What is being given: money, or goods with an estimated value.
    Contribution baseContribution(DonationRequest request);

    // How it is received: validate the method's own fields and decide the status and reference.
    DonationOutcome process(DonationRequest request);
}
