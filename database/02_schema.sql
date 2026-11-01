/* ---------------------------------------------------------------
   HopeBridge - step 2 of 3 : the schema
   WARNING: this script rebuilds every table, so it wipes existing
   data. Run it on a fresh setup (or when you want a clean slate).
   --------------------------------------------------------------- */
USE HopeBridgeDB;
GO

-- Children first, then parents, so the foreign keys never block a drop.
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS volunteer_hours;
DROP TABLE IF EXISTS event_registrations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS donations;
DROP TABLE IF EXISTS campaigns;
DROP TABLE IF EXISTS volunteer_availability;
DROP TABLE IF EXISTS volunteer_skills;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS donation_categories;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS event_categories;
DROP TABLE IF EXISTS availability_slots;
GO

/* ===============================================================
   Lookup tables
   They all share the exact same shape on purpose: one DAO, one
   controller and one pair of pages manage every one of them.
   =============================================================== */
CREATE TABLE donation_categories (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(80)  NOT NULL CONSTRAINT uq_donation_categories_name UNIQUE,
    description NVARCHAR(255) NULL,
    is_active   BIT           NOT NULL DEFAULT 1,
    created_at  DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);

CREATE TABLE skills (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(80)  NOT NULL CONSTRAINT uq_skills_name UNIQUE,
    description NVARCHAR(255) NULL,
    is_active   BIT           NOT NULL DEFAULT 1,
    created_at  DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);

CREATE TABLE event_categories (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(80)  NOT NULL CONSTRAINT uq_event_categories_name UNIQUE,
    description NVARCHAR(255) NULL,
    is_active   BIT           NOT NULL DEFAULT 1,
    created_at  DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);

CREATE TABLE availability_slots (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(80)  NOT NULL CONSTRAINT uq_availability_slots_name UNIQUE,
    description NVARCHAR(255) NULL,
    is_active   BIT           NOT NULL DEFAULT 1,
    created_at  DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);
GO

/* ===============================================================
   People
   One table for every kind of user. The role column tells the
   UserFactory which class (Donor, Volunteer, Staff) to build.
   =============================================================== */
CREATE TABLE users (
    id                INT IDENTITY(1,1) PRIMARY KEY,
    full_name         NVARCHAR(120) NOT NULL,
    email             NVARCHAR(160) NOT NULL CONSTRAINT uq_users_email UNIQUE,
    password_hash     NVARCHAR(255) NOT NULL,
    role              VARCHAR(20)   NOT NULL CONSTRAINT ck_users_role CHECK (role IN ('DONOR', 'VOLUNTEER', 'STAFF')),
    phone             NVARCHAR(30)  NULL,
    preferred_channel VARCHAR(20)   NOT NULL,
    donor_tier        VARCHAR(20)   NOT NULL DEFAULT 'SUPPORTER',
    created_at        DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);

CREATE TABLE volunteer_skills (
    user_id  INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id INT NOT NULL REFERENCES skills(id),
    CONSTRAINT pk_volunteer_skills PRIMARY KEY (user_id, skill_id)
);

CREATE TABLE volunteer_availability (
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    slot_id INT NOT NULL REFERENCES availability_slots(id),
    CONSTRAINT pk_volunteer_availability PRIMARY KEY (user_id, slot_id)
);
GO

/* ===============================================================
   Donations
   =============================================================== */
CREATE TABLE campaigns (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    title         NVARCHAR(140)  NOT NULL,
    summary       NVARCHAR(400)  NOT NULL,
    goal_amount   DECIMAL(12, 2) NOT NULL CONSTRAINT ck_campaigns_goal CHECK (goal_amount > 0),
    raised_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    category_id   INT            NOT NULL REFERENCES donation_categories(id),
    ends_on       DATE           NULL,
    is_active     BIT            NOT NULL DEFAULT 1,
    created_at    DATETIME2      NOT NULL DEFAULT SYSDATETIME()
);

-- base_amount is what the donor typed; total_amount is what the Decorator chain produced.
CREATE TABLE donations (
    id                    INT IDENTITY(1,1) PRIMARY KEY,
    donor_id              INT            NOT NULL REFERENCES users(id),
    campaign_id           INT            NULL     REFERENCES campaigns(id),
    category_id           INT            NOT NULL REFERENCES donation_categories(id),
    method                VARCHAR(20)    NOT NULL,
    base_amount           DECIMAL(12, 2) NOT NULL,
    total_amount          DECIMAL(12, 2) NOT NULL,
    tax_deductible_amount DECIMAL(12, 2) NOT NULL,
    description           NVARCHAR(400)  NOT NULL,
    add_ons               VARCHAR(100)   NULL,
    item_description      NVARCHAR(255)  NULL,
    check_number          NVARCHAR(40)   NULL,
    status                VARCHAR(30)    NOT NULL,
    reference             NVARCHAR(40)   NOT NULL,
    receipt_number        NVARCHAR(40)   NULL,
    created_at            DATETIME2      NOT NULL DEFAULT SYSDATETIME()
);
CREATE INDEX ix_donations_donor ON donations(donor_id, created_at DESC);
GO

/* ===============================================================
   Events
   =============================================================== */
CREATE TABLE events (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    title       NVARCHAR(140) NOT NULL,
    summary     NVARCHAR(600) NOT NULL,
    category_id INT           NOT NULL REFERENCES event_categories(id),
    location    NVARCHAR(160) NOT NULL,
    starts_at   DATETIME2     NOT NULL,
    capacity    INT           NOT NULL CONSTRAINT ck_events_capacity CHECK (capacity > 0),
    created_by  INT           NOT NULL REFERENCES users(id),
    updated_at  DATETIME2     NULL,
    created_at  DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);

CREATE TABLE event_registrations (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    event_id      INT         NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id       INT         NOT NULL REFERENCES users(id),
    ticket_code   VARCHAR(20) NOT NULL CONSTRAINT uq_event_registrations_ticket UNIQUE,
    attended      BIT         NOT NULL DEFAULT 0,
    registered_at DATETIME2   NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT uq_event_registrations_once UNIQUE (event_id, user_id)
);
GO

/* ===============================================================
   Volunteering
   =============================================================== */
CREATE TABLE volunteer_hours (
    id           INT IDENTITY(1,1) PRIMARY KEY,
    volunteer_id INT           NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_id     INT           NULL     REFERENCES events(id),
    work_date    DATE          NOT NULL,
    hours        DECIMAL(5, 2) NOT NULL CONSTRAINT ck_volunteer_hours_range CHECK (hours > 0 AND hours <= 24),
    note         NVARCHAR(255) NULL,
    created_at   DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);
GO

/* ===============================================================
   Communication outbox
   Every message a CommunicationStrategy formats lands here, tagged
   with the channel it was shaped for (EMAIL, SMS, SOCIAL).
   =============================================================== */
CREATE TABLE notifications (
    id         INT IDENTITY(1,1) PRIMARY KEY,
    user_id    INT            NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    channel    VARCHAR(20)    NOT NULL,
    subject    NVARCHAR(160)  NOT NULL,
    body       NVARCHAR(1000) NOT NULL,
    is_read    BIT            NOT NULL DEFAULT 0,
    created_at DATETIME2      NOT NULL DEFAULT SYSDATETIME()
);
CREATE INDEX ix_notifications_user ON notifications(user_id, created_at DESC);
GO
