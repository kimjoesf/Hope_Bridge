package com.hopebridge.model.service;

import com.hopebridge.model.dao.UserDao;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.factory.CommunicationStrategyFactory;
import com.hopebridge.model.strategy.communication.CommunicationStrategy;
import com.hopebridge.model.support.Rules;

import java.util.List;

/**
 * A user looking after their own account: their details and how we reach them.
 */
public class ProfileService {

    private final UserDao userDao;
    private final NotificationService notificationService;

    public ProfileService(UserDao userDao, NotificationService notificationService) {
        this.userDao = userDao;
        this.notificationService = notificationService;
    }

    public void updateDetails(User user, String fullName, String phone) {
        user.setFullName(Rules.requireText(fullName, "Full name", 120));
        user.setPhone(Rules.optionalText(phone, "Phone", 30));
        userDao.updateProfile(user);
    }

    // Client requirement: people choose their channel. This is the Strategy being swapped at runtime:
    // the factory builds the new behavior and the setter plugs it into the user.
    public void changeChannel(User user, String channelCode) {
        user.setCommunicationStrategy(CommunicationStrategyFactory.create(channelCode));
        userDao.updateChannel(user);
        // This confirmation already travels through the NEW strategy - you can see the format change in the inbox.
        notificationService.send(user, "Your contact preference was updated",
                "From now on we will reach you by " + user.getCommunicationStrategy().getLabel().toLowerCase()
                        + ". You can change this any time from your control panel.");
    }

    public List<CommunicationStrategy> availableChannels() {
        return CommunicationStrategyFactory.availableStrategies();
    }
}