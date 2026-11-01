package com.hopebridge;

import com.hopebridge.config.AppConfig;
import com.hopebridge.controller.AuthController;
import com.hopebridge.controller.BaseController;
import com.hopebridge.controller.ControllerSupport;
import com.hopebridge.controller.DonationController;
import com.hopebridge.controller.ErrorController;
import com.hopebridge.controller.EventController;
import com.hopebridge.controller.EventManagementController;
import com.hopebridge.controller.HomeController;
import com.hopebridge.controller.LookupController;
import com.hopebridge.controller.NotificationController;
import com.hopebridge.controller.PanelController;
import com.hopebridge.controller.ProfileController;
import com.hopebridge.controller.StaffController;
import com.hopebridge.controller.VolunteerController;
import com.hopebridge.controller.filter.AuthFilter;
import com.hopebridge.controller.filter.CsrfFilter;
import com.hopebridge.model.dao.CampaignDao;
import com.hopebridge.model.dao.DonationDao;
import com.hopebridge.model.dao.EventDao;
import com.hopebridge.model.dao.LookupDao;
import com.hopebridge.model.dao.NotificationDao;
import com.hopebridge.model.dao.RegistrationDao;
import com.hopebridge.model.dao.StatsDao;
import com.hopebridge.model.dao.UserDao;
import com.hopebridge.model.dao.VolunteerDao;
import com.hopebridge.model.dao.VolunteerHoursDao;
import com.hopebridge.model.db.Database;
import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.observer.AttendeeNotifier;
import com.hopebridge.model.observer.CampaignProgressUpdater;
import com.hopebridge.model.observer.DonationTracker;
import com.hopebridge.model.observer.DonorTierUpdater;
import com.hopebridge.model.observer.EventBoard;
import com.hopebridge.model.observer.LargeGiftAlert;
import com.hopebridge.model.observer.ReceiptIssuer;
import com.hopebridge.model.observer.ThankYouNotifier;
import com.hopebridge.model.observer.VolunteerCallout;
import com.hopebridge.model.service.AuthService;
import com.hopebridge.model.service.DonationService;
import com.hopebridge.model.service.EventService;
import com.hopebridge.model.service.ImpactService;
import com.hopebridge.model.service.LookupService;
import com.hopebridge.model.service.NotificationService;
import com.hopebridge.model.service.ProfileService;
import com.hopebridge.model.service.VolunteerService;
import com.hopebridge.model.support.PasswordHasher;
import com.hopebridge.server.WebServer;
import com.hopebridge.view.ViewRenderer;

import java.util.List;

/**
 * The starting point - and the one place where all the objects are created and connected.
 *
 * Reading main() top to bottom tells the whole story of the app:
 * the Model is built first, then the View, then the Controllers that join the two.
 */
public class Application {

    public static void main(String[] args) throws Exception {
        AppConfig config = AppConfig.getInstance();

        //  MODEL
         // Every DAO shares one executor, which asks the Singleton (singleton is a design pattern that ensures a class can only ever have one instance created in the entire application) database for connections.
         // just to let you know this is an executor okay so it isn't a pool it is just a worker where any Dao needs to run a query it asks it to do it okay, while we do use pool which is simply a technique to keep some connections alive and ready to use instead of creating a new connection each time a request comes in.
        SqlExecutor sql = new SqlExecutor(Database.getInstance());
        UserDao userDao = new UserDao(sql);
        DonationDao donationDao = new DonationDao(sql);
        CampaignDao campaignDao = new CampaignDao(sql);
        EventDao eventDao = new EventDao(sql);
        RegistrationDao registrationDao = new RegistrationDao(sql);
        VolunteerDao volunteerDao = new VolunteerDao(sql);
        VolunteerHoursDao volunteerHoursDao = new VolunteerHoursDao(sql);
        NotificationDao notificationDao = new NotificationDao(sql);
        LookupDao lookupDao = new LookupDao(sql);
        StatsDao statsDao = new StatsDao(sql);

        NotificationService notificationService = new NotificationService(notificationDao, userDao);
        LookupService lookupService = new LookupService(lookupDao);

        // OBSERVER wiring, just like the lecture's WeatherStation: create the subject first,
        // then create each observer and pass it the subject so it can register itself.
        // what happens is that when a donations comes the subject notifies all observers to work at the back ground .
        // is issued before anyone mentions it.
        DonationTracker donationTracker = new DonationTracker(); // the subject 
        // the observers
        new ReceiptIssuer(donationTracker, donationDao);
        new CampaignProgressUpdater(donationTracker, campaignDao);
        new ThankYouNotifier(donationTracker, notificationService);
        new DonorTierUpdater(donationTracker, userDao, donationDao, notificationService);
        new LargeGiftAlert(donationTracker, notificationService);

        EventBoard eventBoard = new EventBoard();
        new AttendeeNotifier(eventBoard, registrationDao, notificationService);
        new VolunteerCallout(eventBoard, notificationService);

        AuthService authService = new AuthService(userDao, new PasswordHasher(), notificationService);
        ProfileService profileService = new ProfileService(userDao, notificationService);
        DonationService donationService = new DonationService(donationDao, campaignDao, lookupService, donationTracker);
        EventService eventService = new EventService(eventDao, registrationDao, lookupService, notificationService, eventBoard);
        VolunteerService volunteerService = new VolunteerService(volunteerDao, volunteerHoursDao, eventDao, lookupService);
        ImpactService impactService = new ImpactService(statsDao);

        // VIEW
        ViewRenderer views = new ViewRenderer(config.getBoolean("views.cache")); // configuring the engine step .

        //  CONTROLLER
        ControllerSupport support = new ControllerSupport(views, authService, notificationService);
        List<BaseController> controllers = List.of(
                new HomeController(support, donationService, eventService, impactService),
                new AuthController(support),
                new PanelController(support, donationService, eventService, volunteerService, impactService, lookupService),
                new DonationController(support, donationService, lookupService),
                new ProfileController(support, profileService),
                new NotificationController(support),
                new VolunteerController(support, volunteerService, lookupService, eventService),
                new EventController(support, eventService),
                new EventManagementController(support, eventService, lookupService),
                new LookupController(support, lookupService),
                new StaffController(support),
                new ErrorController(support));
        // WEB SERVER
        // it starts the webserver on the port specified in the config file, adds filters to protect sensitive routes,and Mount Controllers: Maps every controller to its respective URL paths .
        WebServer server = new WebServer(config.getInt("server.port"));
        server.addFilter("csrf", new CsrfFilter(), "/*");
        server.addFilter("auth", new AuthFilter(), "/panel/*", "/donate", "/events/register", "/events/unregister");
        // here is the *controller mounting
        controllers.forEach(controller -> server.mount(controller.getClass().getSimpleName(), controller, controller.paths()));
        for (int status : new int[]{403, 404, 405, 500}) {
            server.addErrorPage(status, ErrorController.PATH);
        }
        server.addExceptionPage(ErrorController.PATH);
        server.start();
    }
}

// * controller mounting means telling the webserver which java controller class is responsible for which URL .