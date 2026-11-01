package com.hopebridge.controller;

import com.hopebridge.model.entity.User;
import com.hopebridge.model.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * CONTROLLER - a user's own details and their communication preference.
 */
public class ProfileController extends BaseController {

    private static final String PAGE = "/panel/profile";

    private final ProfileService profileService;

    public ProfileController(ControllerSupport support, ProfileService profileService) {
        super(support);
        this.profileService = profileService;
        get(PAGE, this::show);
        post(PAGE, this::saveDetails);
        post(PAGE + "/channel", this::changeChannel);
    }

    private void show(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireUser(request);
        render(request, response, "panel/profile", model("channels", profileService.availableChannels()));
    }

    private void saveDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireUser(request);
        profileService.updateDetails(user, RequestParams.text(request, "fullName"), RequestParams.text(request, "phone"));
        success(request, "Your details were saved.");
        redirect(response, PAGE);
    }

    private void changeChannel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireUser(request);
        profileService.changeChannel(user, RequestParams.text(request, "channel"));
        success(request, "Done! We will reach you by " + user.getCommunicationStrategy().getLabel().toLowerCase()
                + " from now on. Peek at your inbox to see the new format.");
        redirect(response, PAGE);
    }
}
