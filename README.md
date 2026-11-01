# HopeBridge - Non-Profit Management System (Phase 1)

A Java MVC web application for a non-profit: donations, volunteers, events and communication,
built around the design patterns required for **Phase 1** of the CSE336 major task.

**Stack:** Java 21 · embedded Tomcat (plain Servlets) · Thymeleaf HTML/CSS views · SQL Server + JDBC (HikariCP pool) · Maven Wrapper

---

## Run it

1. **Create the database** (SQL Server on `localhost`, TCP port `1434`, Windows authentication):
   ```
   database\setup.cmd
   ```
   or open `database/01_create_database.sql`, `02_schema.sql`, `03_seed.sql` in SSMS and run them in order.
2. **Start the app**:
   ```
   mvnw.cmd
   ```
3. Open **http://localhost:8080**

| Demo account | Role | Password |
|---|---|---|
| `staff@hopebridge.org` | Staff (lookup tables, events) | `Hope@2026` |
| `donor@hopebridge.org` | Donor (Silver tier, gifts, receipts) | `Hope@2026` |
| `volunteer@hopebridge.org` | Volunteer (skills, hours, certificate) | `Hope@2026` |

Settings (port, connection string, SQL login instead of Windows auth) live in
`src/main/resources/application.properties`. Run the tests with `mvnw.cmd test`.

---

## Phase 1 checklist

| Item | Where |
|---|---|
| **Class diagram** | `docs/class-diagram.png` (source: `docs/class-diagram.puml`) |
| **Strategy** | `model/strategy/donation` - `DonationMethod` + `OnlineMethod`, `CheckMethod`, `InKindMethod`; context `DonationProcessor` (like the lab's *Payment*).<br>`model/strategy/communication` - `CommunicationStrategy` + `EmailStrategy`, `SmsStrategy`, `SocialMediaStrategy`; context is the abstract `User` (like the lab's *Duck*): subclasses set a default in their constructor, `setCommunicationStrategy()` swaps it at runtime from the profile page. |
| **Observer** | `model/observer` - `Subject` / `Observer` interfaces (lecture's WeatherData design). `DonationTracker` notifies `ReceiptIssuer`, `CampaignProgressUpdater`, `ThankYouNotifier`, `DonorTierUpdater`, `LargeGiftAlert`. `EventBoard` notifies `AttendeeNotifier`, `VolunteerCallout`. Observers register themselves in their constructor; wiring is in `Application.main` (like WeatherStation). |
| **Decorator** | `model/decorator` - `Contribution` (component, like *Beverage*), `MoneyContribution` / `InKindContribution` (concrete components), `ContributionAddOn` (decorator), `CoverFeesAddOn`, `EmployerMatchAddOn`, `DedicationAddOn` (concrete decorators). |
| **Factory** | `model/factory` - `UserFactory`, `DonationMethodFactory`, `CommunicationStrategyFactory`, `AddOnFactory` (Simple Factory with a registry: one line per new type). |
| **Singleton** | `model/db/Database` (connection pool) and `config/AppConfig` - lazy `getInstance()` exactly as in the lecture, `synchronized` for Tomcat's threads. |
| **MVC** | `model/` (entities, DAOs, services, patterns - no servlet/HTML code), `view/` + `resources/templates` (rendering only), `controller/` (read request → call model → choose view). |
| **User Control Panel** | `/panel` - role-aware dashboard, donations & printable receipts, inbox, profile & channel preference, tickets; volunteer skills/hours/certificate; staff operations. |
| **CRUD lookup tables** | `/panel/lookups` (staff) - Donation Categories, Skills, Event Categories, Availability Slots through one generic `LookupDao` / `LookupService` / `LookupController` driven by the `LookupTable` enum. |

### Client requirements (collected from users)
1. *As a donor, I want to see how close each campaign is to its goal* → campaign progress bars, updated by `CampaignProgressUpdater`.
2. *As a donor, I want my loyalty recognised* → Supporter / Bronze / Silver / Gold / Platinum tiers with progress to the next tier, updated by `DonorTierUpdater`.
3. *As a user, I want to choose how you contact me* → Email / SMS / Social preference (Strategy swapped at runtime); the inbox shows each message in its channel's format.
4. *As a donor or volunteer, I want official documents* → printable tax receipts and volunteer certificates (Print / Save as PDF).

---
