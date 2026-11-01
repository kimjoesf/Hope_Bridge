package com.hopebridge.model.service;

import com.hopebridge.model.dao.UserDao;
import com.hopebridge.model.entity.Role;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.exception.ValidationException;
import com.hopebridge.model.factory.UserFactory;
import com.hopebridge.model.support.PasswordHasher;
import com.hopebridge.model.support.Rules;

import java.util.List;
import java.util.Optional;

/**
 * Signing up and signing in.
 */
public class AuthService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserDao userDao;
    private final PasswordHasher passwordHasher;
    private final NotificationService notificationService;

    public AuthService(UserDao userDao, PasswordHasher passwordHasher, NotificationService notificationService) {
        this.userDao = userDao;
        this.passwordHasher = passwordHasher;
        this.notificationService = notificationService;
    }

    public User register(String fullName, String email, String password, String roleCode, String phone) {
        // The factory builds a Donor or a Volunteer from what the person picked on the form.
        User user = UserFactory.create(roleCode);
        Rules.require(user.getRole().isSelfRegistrable(), "Staff accounts are created by the organization.");
        return createAccount(user, fullName, email, password, phone);
    }

    // The organization's own door: only a signed-in staff member reaches this (the controller checks the role).
    public User createStaff(String fullName, String email, String password, String phone) {
        return createAccount(UserFactory.create(Role.STAFF.name()), fullName, email, password, phone);
    }

    public List<User> staffMembers() {
        return userDao.findByRole(Role.STAFF);
    }

    // Both doors end here, so every account is validated, hashed and welcomed the same way.
    private User createAccount(User user, String fullName, String email, String password, String phone) {
        String name = Rules.requireText(fullName, "Full name", 120);
        String cleanEmail = Rules.requireEmail(email);
        Rules.require(password != null && password.length() >= MIN_PASSWORD_LENGTH,"The password needs at least " + MIN_PASSWORD_LENGTH + " characters.");
        Rules.require(userDao.findByEmail(cleanEmail).isEmpty(), "An account with this email already exists.");

        user.setFullName(name);
        user.setEmail(cleanEmail);
        user.setPhone(Rules.optionalText(phone, "Phone", 30));
        user.setPasswordHash(passwordHasher.hash(password));
        userDao.insert(user);

        notificationService.send(user, "Welcome to HopeBridge",
                "We are so glad you joined us as a " + user.getRole().getLabel().toLowerCase()
                        + ". Your control panel is ready: you can update your details and choose how we keep in touch at any time.");
        return user;
    }

    // One message for both "no such email" and "wrong password" .
    public User authenticate(String email, String password) {
        String cleanEmail = email == null ? "" : email.trim().toLowerCase();
        return userDao.findByEmail(cleanEmail).filter(user -> passwordHasher.matches(password, user.getPasswordHash())).orElseThrow(() -> new ValidationException("That email and password do not match our records."));
    }

    public Optional<User> findById(int id) {
        return userDao.findById(id);
    }
}
