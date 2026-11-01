/* ---------------------------------------------------------------
   HopeBridge - step 3 of 3 : demo data
   Every demo account uses the password:  Hope@2026

     staff@hopebridge.org      Staff      (lookup tables, events)
     donor@hopebridge.org      Donor      (Silver tier, a few gifts)
     volunteer@hopebridge.org  Volunteer  (skills, logged hours)
   --------------------------------------------------------------- */
USE HopeBridgeDB;
GO

SET NOCOUNT ON;

DECLARE @password NVARCHAR(255) = N'pbkdf2_sha256$210000$VJ4iNeLNAlPIeBjGj8AAYg==$uIY3qrWIl7XKuQWF1UK/COib2t9Znt0LdTLTMrJF+Lk=';
DECLARE @today DATETIME2 = CAST(CAST(SYSDATETIME() AS DATE) AS DATETIME2);

/* ----------------------------- lookups ----------------------------- */
INSERT INTO donation_categories (name, description) VALUES
    (N'Food Security',     N'Hot meals and food parcels for families in need.'),
    (N'Education',         N'School kits, tutoring and scholarships.'),
    (N'Healthcare',        N'Mobile clinics, medicine and check-ups.'),
    (N'Shelter & Housing', N'Blankets, repairs and safe places to sleep.'),
    (N'Emergency Relief',  N'Fast response when disaster strikes.'),
    (N'Clean Water',       N'Wells, filters and hygiene programs.');

INSERT INTO skills (name, description) VALUES
    (N'Teaching & Tutoring', N'Helping children and adults learn.'),
    (N'First Aid',           N'Certified to give basic medical help.'),
    (N'Driving',             N'Can transport goods or people.'),
    (N'Cooking',             N'Preparing meals at scale.'),
    (N'Fundraising',         N'Comfortable asking for support.'),
    (N'Photography',         N'Capturing our events and stories.'),
    (N'IT Support',          N'Fixing computers and setting up tools.'),
    (N'Event Setup',         N'Stages, chairs, signage - the heavy lifting.');

INSERT INTO event_categories (name, description) VALUES
    (N'Fundraiser',       N'Events that raise money for our programs.'),
    (N'Workshop',         N'Hands-on learning sessions.'),
    (N'Outreach Program', N'Going out into the community.'),
    (N'Community Drive',  N'Collecting goods for people who need them.');

INSERT INTO availability_slots (name, description) VALUES
    (N'Weekday mornings',   N'Monday to Friday, 8 AM - 12 PM'),
    (N'Weekday afternoons', N'Monday to Friday, 12 PM - 5 PM'),
    (N'Weekday evenings',   N'Monday to Friday, after 5 PM'),
    (N'Weekends',           N'Saturday and Sunday, any time');

/* ------------------------------ people ----------------------------- */
INSERT INTO users (full_name, email, password_hash, role, phone, preferred_channel) VALUES
    (N'Mariam Adel',    N'staff@hopebridge.org',     @password, 'STAFF',     N'+20 100 555 0101', 'EMAIL'),
    (N'Omar Khaled',    N'donor@hopebridge.org',     @password, 'DONOR',     N'+20 100 555 0102', 'EMAIL'),
    (N'Salma Hassan',   N'volunteer@hopebridge.org', @password, 'VOLUNTEER', N'+20 100 555 0103', 'SMS'),
    (N'Youssef Nabil',  N'youssef@example.com',      @password, 'DONOR',     NULL,                'SOCIAL'),
    (N'Nour Farid',     N'nour@example.com',         @password, 'DONOR',     NULL,                'EMAIL'),
    (N'Karim Mostafa',  N'karim@example.com',        @password, 'VOLUNTEER', N'+20 100 555 0106', 'SMS');

DECLARE @staff     INT = (SELECT id FROM users WHERE email = N'staff@hopebridge.org');
DECLARE @donor     INT = (SELECT id FROM users WHERE email = N'donor@hopebridge.org');
DECLARE @volunteer INT = (SELECT id FROM users WHERE email = N'volunteer@hopebridge.org');
DECLARE @youssef   INT = (SELECT id FROM users WHERE email = N'youssef@example.com');
DECLARE @nour      INT = (SELECT id FROM users WHERE email = N'nour@example.com');
DECLARE @karim     INT = (SELECT id FROM users WHERE email = N'karim@example.com');

INSERT INTO volunteer_skills (user_id, skill_id)
SELECT @volunteer, id FROM skills WHERE name IN (N'First Aid', N'Teaching & Tutoring', N'Event Setup')
UNION ALL
SELECT @karim, id FROM skills WHERE name IN (N'Driving', N'Cooking');

INSERT INTO volunteer_availability (user_id, slot_id)
SELECT @volunteer, id FROM availability_slots WHERE name IN (N'Weekends', N'Weekday evenings')
UNION ALL
SELECT @karim, id FROM availability_slots WHERE name = N'Weekends';

/* ---------------------------- campaigns ---------------------------- */
-- raised_amount starts with what was collected offline; the online gifts below are added on top.
INSERT INTO campaigns (title, summary, goal_amount, raised_amount, category_id, ends_on) VALUES
    (N'Winter Meals for 1,000 Families',
     N'Warm, nutritious meals delivered every week of the winter to families who would otherwise go without.',
     25000, 14200, (SELECT id FROM donation_categories WHERE name = N'Food Security'), CAST(DATEADD(DAY, 45, @today) AS DATE)),
    (N'Back to School Kits',
     N'Backpacks, notebooks and uniforms so every child starts the year ready to learn.',
     15000, 9800, (SELECT id FROM donation_categories WHERE name = N'Education'), CAST(DATEADD(DAY, 60, @today) AS DATE)),
    (N'Mobile Health Clinic',
     N'A fully equipped clinic on wheels bringing doctors to villages without a hospital nearby.',
     40000, 12500, (SELECT id FROM donation_categories WHERE name = N'Healthcare'), CAST(DATEADD(DAY, 90, @today) AS DATE)),
    (N'Clean Water Wells',
     N'Drilling wells and installing filters so whole communities get safe drinking water.',
     30000, 21000, (SELECT id FROM donation_categories WHERE name = N'Clean Water'), CAST(DATEADD(DAY, 120, @today) AS DATE));

/* ------------------------------ events ----------------------------- */
INSERT INTO events (title, summary, category_id, location, starts_at, capacity, created_by) VALUES
    (N'First Aid Basics Workshop',
     N'Learn CPR, bandaging and how to respond in the first critical minutes. Certificates for all participants.',
     (SELECT id FROM event_categories WHERE name = N'Workshop'), N'HopeBridge Community Center, Hall B',
     DATEADD(HOUR, 10, DATEADD(DAY, 5, @today)), 30, @staff),
    (N'Neighborhood Food Drive',
     N'Help us collect, sort and pack food parcels for 300 families. Bring a friend!',
     (SELECT id FROM event_categories WHERE name = N'Community Drive'), N'Al-Nour Square, Nasr City',
     DATEADD(HOUR, 9, DATEADD(DAY, 9, @today)), 60, @staff),
    (N'Charity Gala Dinner',
     N'An evening of music, stories and generosity in support of the Mobile Health Clinic.',
     (SELECT id FROM event_categories WHERE name = N'Fundraiser'), N'The Grand Nile Ballroom',
     DATEADD(HOUR, 19, DATEADD(DAY, 12, @today)), 150, @staff),
    (N'Youth Mentoring Outreach',
     N'Spend a morning with teenagers from partner schools talking careers, goals and confidence.',
     (SELECT id FROM event_categories WHERE name = N'Outreach Program'), N'Future Leaders School, Giza',
     DATEADD(HOUR, 11, DATEADD(DAY, 20, @today)), 25, @staff),
    (N'Fun Run for Clean Water',
     N'A 5K run along the Corniche. Every kilometer you run brings water to a family in need.',
     (SELECT id FROM event_categories WHERE name = N'Fundraiser'), N'Corniche El Maadi',
     DATEADD(HOUR, 7, DATEADD(DAY, 30, @today)), 400, @staff),
    (N'Spring Clean-Up Day',
     N'We cleaned and repainted the community library together.',
     (SELECT id FROM event_categories WHERE name = N'Outreach Program'), N'Shubra Public Library',
     DATEADD(HOUR, 9, DATEADD(DAY, -20, @today)), 40, @staff);

DECLARE @workshop INT = (SELECT id FROM events WHERE title = N'First Aid Basics Workshop');
DECLARE @drive    INT = (SELECT id FROM events WHERE title = N'Neighborhood Food Drive');
DECLARE @gala     INT = (SELECT id FROM events WHERE title = N'Charity Gala Dinner');
DECLARE @cleanup  INT = (SELECT id FROM events WHERE title = N'Spring Clean-Up Day');

INSERT INTO event_registrations (event_id, user_id, ticket_code) VALUES
    (@gala,     @donor,     'EVT-GALA7KQ2'),
    (@drive,    @donor,     'EVT-DRV4MZ8P'),
    (@workshop, @volunteer, 'EVT-WRK9TB3X'),
    (@drive,    @volunteer, 'EVT-DRV2HN6C'),
    (@drive,    @karim,     'EVT-DRV8RW5L');

/* ---------------------------- volunteering ------------------------- */
INSERT INTO volunteer_hours (volunteer_id, event_id, work_date, hours, note) VALUES
    (@volunteer, @cleanup, CAST(DATEADD(DAY, -20, @today) AS DATE), 5.00, N'Painted the reading room'),
    (@volunteer, NULL,     CAST(DATEADD(DAY, -14, @today) AS DATE), 3.50, N'Sorted donations at the warehouse'),
    (@volunteer, NULL,     CAST(DATEADD(DAY, -7,  @today) AS DATE), 4.00, N'Tutored grade 5 math'),
    (@volunteer, NULL,     CAST(DATEADD(DAY, -2,  @today) AS DATE), 2.50, N'Packed winter meal boxes'),
    (@karim,     @cleanup, CAST(DATEADD(DAY, -20, @today) AS DATE), 6.00, N'Drove supplies and helped paint');

/* ----------------------------- donations --------------------------- */
-- These rows are exactly what the Strategy + Decorator code would have produced.
INSERT INTO donations (donor_id, campaign_id, category_id, method, base_amount, total_amount, tax_deductible_amount,
                       description, add_ons, item_description, check_number, status, reference, created_at) VALUES
    (@donor, 1, (SELECT category_id FROM campaigns WHERE id = 1), 'ONLINE', 250.00, 257.50, 257.50,
     N'Monetary gift, processing fees covered', 'COVER_FEES', NULL, NULL, 'CONFIRMED', N'TXN-K7P3QH9WZ2', DATEADD(DAY, -40, SYSDATETIME())),
    (@donor, 2, (SELECT category_id FROM campaigns WHERE id = 2), 'CHECK', 500.00, 500.00, 500.00,
     N'Monetary gift', NULL, NULL, N'10457', 'PENDING_CLEARANCE', N'CHK-10457', DATEADD(DAY, -10, SYSDATETIME())),
    (@donor, 3, (SELECT category_id FROM campaigns WHERE id = 3), 'ONLINE', 300.00, 618.00, 309.00,
     N'Monetary gift, processing fees covered, matched by Vodafone Egypt', 'COVER_FEES,EMPLOYER_MATCH', NULL, NULL, 'CONFIRMED', N'TXN-M4RX8CJ6TN', DATEADD(DAY, -3, SYSDATETIME())),
    (@donor, NULL, (SELECT id FROM donation_categories WHERE name = N'Shelter & Housing'), 'IN_KIND', 400.00, 400.00, 400.00,
     N'In-kind gift: 20 winter blankets', NULL, N'20 winter blankets', NULL, 'AWAITING_DROP_OFF', N'INK-BV6N2QXA', DATEADD(DAY, -1, SYSDATETIME())),
    (@youssef, 4, (SELECT category_id FROM campaigns WHERE id = 4), 'ONLINE', 1500.00, 1500.00, 1500.00,
     N'Monetary gift, in honour of my grandmother Fatma', 'DEDICATION', NULL, NULL, 'CONFIRMED', N'TXN-Z2HL5PD8KE', DATEADD(DAY, -6, SYSDATETIME())),
    (@nour, 1, (SELECT category_id FROM campaigns WHERE id = 1), 'ONLINE', 75.00, 75.00, 75.00,
     N'Monetary gift', NULL, NULL, NULL, 'CONFIRMED', N'TXN-W9CT3RF7MA', DATEADD(DAY, -2, SYSDATETIME())),
    (@karim, NULL, (SELECT id FROM donation_categories WHERE name = N'Education'), 'ONLINE', 50.00, 50.00, 50.00,
     N'Monetary gift', NULL, NULL, NULL, 'CONFIRMED', N'TXN-Q3JN7VB2XS', DATEADD(DAY, -12, SYSDATETIME()));

-- What the ReceiptIssuer observer does for every new gift.
UPDATE donations
SET receipt_number = CONCAT('HB-', YEAR(created_at), '-', RIGHT('000000' + CAST(id AS VARCHAR(6)), 6));

-- What the CampaignProgressUpdater observer does.
UPDATE c
SET raised_amount = c.raised_amount + ISNULL((SELECT SUM(d.total_amount) FROM donations d WHERE d.campaign_id = c.id), 0)
FROM campaigns c;

-- What the DonorTierUpdater observer does (thresholds match the DonorTier enum).
UPDATE u
SET donor_tier = CASE
                     WHEN g.total >= 10000 THEN 'PLATINUM'
                     WHEN g.total >= 2000  THEN 'GOLD'
                     WHEN g.total >= 500   THEN 'SILVER'
                     WHEN g.total >= 100   THEN 'BRONZE'
                     ELSE 'SUPPORTER'
                 END
FROM users u
JOIN (SELECT donor_id, SUM(tax_deductible_amount) AS total FROM donations GROUP BY donor_id) g ON g.donor_id = u.id;

/* --------------------------- notifications ------------------------- */
-- A few messages so the inboxes are not empty. Each one is shaped for the user's channel.
INSERT INTO notifications (user_id, channel, subject, body, is_read, created_at) VALUES
    (@donor, 'EMAIL', N'Welcome to HopeBridge',
     N'Dear Omar,' + CHAR(10) + CHAR(10) + N'We are so glad you joined us as a donor. Your control panel is ready: you can update your details and choose how we keep in touch at any time.' + CHAR(10) + CHAR(10) + N'With gratitude,' + CHAR(10) + N'The HopeBridge team',
     1, DATEADD(DAY, -41, SYSDATETIME())),
    (@donor, 'EMAIL', N'You are now a Silver supporter!',
     N'Dear Omar,' + CHAR(10) + CHAR(10) + N'Your generosity just moved you up to our Silver tier. Thank you for being such a big part of our work.' + CHAR(10) + CHAR(10) + N'With gratitude,' + CHAR(10) + N'The HopeBridge team',
     0, DATEADD(DAY, -10, SYSDATETIME())),
    (@volunteer, 'SMS', N'Welcome to HopeBridge',
     N'HopeBridge: Welcome to HopeBridge - We are so glad you joined us as a volunteer. Your control panel is ready: you can update your details and choose how we…',
     1, DATEADD(DAY, -30, SYSDATETIME())),
    (@volunteer, 'SMS', N'You''re registered: First Aid Basics Workshop',
     N'HopeBridge: You''re registered: First Aid Basics Workshop - Your ticket code is EVT-WRK9TB3X. See you there!',
     0, DATEADD(DAY, -4, SYSDATETIME())),
    (@staff, 'EMAIL', N'Large gift received',
     N'Dear Mariam,' + CHAR(10) + CHAR(10) + N'A gift worth $1,500.00 just arrived. It might be a good moment for a personal thank-you call.' + CHAR(10) + CHAR(10) + N'With gratitude,' + CHAR(10) + N'The HopeBridge team',
     0, DATEADD(DAY, -6, SYSDATETIME()));

PRINT 'HopeBridge demo data loaded. Sign in with any demo account and the password Hope@2026';
GO
