package com.hopebridge.model.decorator;

/**
 * DECORATOR - the abstract Decorator (the "base decorator ").
 */

public abstract class ContributionAddOn extends Contribution {

    protected final Contribution contribution;

    protected ContributionAddOn(Contribution contribution) {
        this.contribution = contribution;
    }

 // just for Every add-on to say how it changes the description.
    @Override
    public abstract String getDescription();
}
