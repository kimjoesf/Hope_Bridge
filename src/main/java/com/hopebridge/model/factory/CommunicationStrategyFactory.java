package com.hopebridge.model.factory;

import com.hopebridge.model.strategy.communication.CommunicationStrategy;
import com.hopebridge.model.strategy.communication.EmailStrategy;
import com.hopebridge.model.strategy.communication.SmsStrategy;
import com.hopebridge.model.strategy.communication.SocialMediaStrategy;

import java.util.List;

/**
 * FACTORY - turns a saved channel preference ("EMAIL", "SMS", "SOCIAL")
 * into the matching communication strategy object.
 */
public final class CommunicationStrategyFactory {

    private static final TypeRegistry<CommunicationStrategy> CHANNELS = new TypeRegistry<CommunicationStrategy>("channel")
            .register("EMAIL", EmailStrategy::new)
            .register("SMS", SmsStrategy::new)
            .register("SOCIAL", SocialMediaStrategy::new);

    private CommunicationStrategyFactory() {
    }

    public static CommunicationStrategy create(String code) {
        return CHANNELS.create(code);
    }

    public static List<CommunicationStrategy> availableStrategies() {
        return CHANNELS.createAll();
    }
}
