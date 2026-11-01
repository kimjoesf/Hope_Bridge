package com.hopebridge.model.factory;

import com.hopebridge.model.entity.Donor;
import com.hopebridge.model.entity.Role;
import com.hopebridge.model.entity.Staff;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.entity.Volunteer;

/**
 * FACTORY - creates the right kind of User from a role code.
 *
 * The role comes either from the database (when we load a user) or from the sign-up
 * form (user input). Nobody else in the app writes "new Donor()" or "new Volunteer()";
 * they ask the factory, so the decision of WHICH class to build lives in one place.
 */
public final class UserFactory {

    private static final TypeRegistry<User> USERS = new TypeRegistry<User>("role")
            .register("DONOR", Donor::new)
            .register("VOLUNTEER", Volunteer::new)
            .register("STAFF", Staff::new);

    private UserFactory() {
    }

    public static User create(String roleCode) {
        return USERS.create(roleCode);
    }

    public static User create(Role role) {
        return create(role.name());
    }
}
