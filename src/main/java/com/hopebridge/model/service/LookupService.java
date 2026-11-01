package com.hopebridge.model.service;

import com.hopebridge.model.dao.LookupDao;
import com.hopebridge.model.entity.LookupItem;
import com.hopebridge.model.entity.LookupTable;
import com.hopebridge.model.exception.DataAccessException;
import com.hopebridge.model.exception.NotFoundException;
import com.hopebridge.model.exception.ValidationException;
import com.hopebridge.model.support.Rules;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * CRUD for every lookup table, plus the "is this choice still valid?" check the
 * other services use when someone picks a category, a skill or a time slot.
 */
public class LookupService {

    private final LookupDao lookupDao;

    public LookupService(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    public List<LookupItem> all(LookupTable table) {
        return lookupDao.findAll(table);
    }

    public List<LookupItem> active(LookupTable table) {
        return lookupDao.findActive(table);
    }

    public LookupItem find(LookupTable table, int id) {
        return lookupDao.findById(table, id)
                .orElseThrow(() -> new NotFoundException(table.getItemLabel() + " #" + id + " does not exist."));
    }

    public Map<LookupTable, Integer> counts() {
        Map<LookupTable, Integer> counts = new EnumMap<>(LookupTable.class);
        for (LookupTable table : LookupTable.values()) {
            counts.put(table, lookupDao.count(table));
        }
        return counts;
    }

    // A null id means "create", anything else means "edit that row".
    public LookupItem save(LookupTable table, Integer id, String name, String description, boolean active) {
        LookupItem item = id == null ? new LookupItem() : find(table, id);
        item.setName(Rules.requireText(name, "Name", 80));
        item.setDescription(Rules.optionalText(description, "Description", 255));
        item.setActive(active);
        try {
            if (item.isNew()) {
                lookupDao.insert(table, item);
            } else {
                lookupDao.update(table, item);
            }
        } catch (DataAccessException e) {
            if (e.isUniqueViolation()) {
                throw new ValidationException("There is already a " + table.getItemLabel().toLowerCase() + " called \"" + item.getName() + "\".");
            }
            throw e;
        }
        return item;
    }

    // Rows already used by donations, events or volunteers cannot be deleted - they can be deactivated instead.
    public LookupItem delete(LookupTable table, int id) {
        LookupItem item = find(table, id);
        try {
            lookupDao.delete(table, id);
        } catch (DataAccessException e) {
            if (e.isForeignKeyViolation()) {
                throw new ValidationException("\"" + item.getName() + "\" is still in use, so it cannot be deleted. "
                        + "Deactivate it instead - it will disappear from forms but history stays intact.");
            }
            throw e;
        }
        return item;
    }

    public void requireActive(LookupTable table, Integer id, String message) {
        Rules.require(id != null && lookupDao.isActive(table, id), message);
    }
}
