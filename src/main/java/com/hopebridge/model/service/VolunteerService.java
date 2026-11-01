package com.hopebridge.model.service;

import com.hopebridge.model.dao.EventDao;
import com.hopebridge.model.dao.VolunteerDao;
import com.hopebridge.model.dao.VolunteerHoursDao;
import com.hopebridge.model.dto.VolunteerSummary;
import com.hopebridge.model.entity.LookupTable;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.entity.VolunteerShift;
import com.hopebridge.model.support.Rules;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Volunteer management: skills and availability, logged hours, and the certificate.
 */
public class VolunteerService {

    private static final BigDecimal MIN_HOURS = new BigDecimal("0.25");
    private static final BigDecimal MAX_HOURS = new BigDecimal("16");

    private final VolunteerDao volunteerDao;
    private final VolunteerHoursDao hoursDao;
    private final EventDao eventDao;
    private final LookupService lookupService;

    public VolunteerService(VolunteerDao volunteerDao, VolunteerHoursDao hoursDao,
                            EventDao eventDao, LookupService lookupService) {
        this.volunteerDao = volunteerDao;
        this.hoursDao = hoursDao;
        this.eventDao = eventDao;
        this.lookupService = lookupService;
    }

    public List<Integer> skillIds(int volunteerId) {
        return volunteerDao.findSkillIds(volunteerId);
    }

    public List<Integer> slotIds(int volunteerId) {
        return volunteerDao.findSlotIds(volunteerId);
    }

    public List<String> skillNames(int volunteerId) {
        return volunteerDao.findSkillNames(volunteerId);
    }

    public void updateProfile(User volunteer, List<Integer> skillIds, List<Integer> slotIds) {
        skillIds.forEach(id -> lookupService.requireActive(LookupTable.SKILLS, id, "One of the selected skills is no longer offered."));
        slotIds.forEach(id -> lookupService.requireActive(LookupTable.AVAILABILITY_SLOTS, id, "One of the selected time slots is no longer offered."));
        volunteerDao.replaceProfile(volunteer.getId(), skillIds, slotIds);
    }

    public VolunteerShift logHours(User volunteer, LocalDate workDate, BigDecimal hours, Integer eventId, String note) {
        Rules.require(workDate != null, "Please tell us which day you volunteered.");
        Rules.require(!workDate.isAfter(LocalDate.now()), "You can only log hours for today or earlier.");
        Rules.require(workDate.isAfter(LocalDate.now().minusYears(1)), "Hours older than a year need to be added by staff.");
        Rules.require(hours != null && hours.compareTo(MIN_HOURS) >= 0 && hours.compareTo(MAX_HOURS) <= 0,
                "Hours must be between " + MIN_HOURS + " and " + MAX_HOURS + " for a single day.");
        Rules.require(eventId == null || eventDao.findById(eventId).isPresent(), "That event does not exist.");

        VolunteerShift shift = new VolunteerShift();
        shift.setVolunteerId(volunteer.getId());
        shift.setWorkDate(workDate);
        shift.setHours(hours);
        shift.setEventId(eventId);
        shift.setNote(Rules.optionalText(note, "Note", 255));
        hoursDao.insert(shift);
        return shift;
    }

    public List<VolunteerShift> shifts(int volunteerId) {
        return hoursDao.findByVolunteer(volunteerId);
    }

    public VolunteerSummary summary(int volunteerId) {
        return hoursDao.summarize(volunteerId);
    }

    // Stable for the same volunteer, so a reprinted certificate carries the same number.
    public String certificateNumber(User volunteer) {
        return "HB-VOL-" + String.format("%05d", volunteer.getId());
    }
}
