package com.hopebridge.controller;

import com.hopebridge.model.entity.LookupItem;
import com.hopebridge.model.entity.LookupTable;
import com.hopebridge.model.entity.Role;
import com.hopebridge.model.exception.NotFoundException;
import com.hopebridge.model.service.LookupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * CONTROLLER - CRUD for every lookup table through the same four actions.
 * Which table we are working on travels in the "table" parameter (its slug).
 */
public class LookupController extends BaseController {

    private static final String LIST = "/panel/lookups";
    private static final List<LookupTable> TABLES = List.of(LookupTable.values());

    private final LookupService lookupService;

    public LookupController(ControllerSupport support, LookupService lookupService) {
        super(support);
        this.lookupService = lookupService;
        get(LIST, this::list);
        get(LIST + "/edit", this::showForm);
        post(LIST + "/save", this::save);
        post(LIST + "/delete", this::delete);
    }

    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireRole(request, Role.STAFF);
        LookupTable table = selectedTable(request);
        render(request, response, "panel/lookups", model(
                "tables", TABLES,
                "table", table,
                "items", lookupService.all(table),
                "counts", lookupService.counts()));
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireRole(request, Role.STAFF);
        LookupTable table = selectedTable(request);
        Integer id = RequestParams.integer(request, "id");
        render(request, response, "panel/lookup-form", model(
                "tables", TABLES,
                "table", table,
                "item", id == null ? new LookupItem() : lookupService.find(table, id)));
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireRole(request, Role.STAFF);
        LookupTable table = selectedTable(request);
        LookupItem item = lookupService.save(table,
                RequestParams.integer(request, "id"),
                RequestParams.text(request, "name"),
                RequestParams.text(request, "description"),
                RequestParams.checked(request, "active"));
        success(request, table.getItemLabel() + " \"" + item.getName() + "\" was saved.");
        redirect(response, listUrl(table));
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireRole(request, Role.STAFF);
        LookupTable table = selectedTable(request);
        LookupItem item = lookupService.delete(table, requiredId(request, "id"));
        success(request, table.getItemLabel() + " \"" + item.getName() + "\" was deleted.");
        redirect(response, listUrl(table));
    }

    // No table chosen yet? Start with the first one.
    private static LookupTable selectedTable(HttpServletRequest request) {
        String slug = RequestParams.text(request, "table");
        if (slug == null || slug.isEmpty()) {
            return TABLES.get(0);
        }
        return LookupTable.fromSlug(slug).orElseThrow(() -> new NotFoundException("Unknown lookup table " + slug));
    }

    private static String listUrl(LookupTable table) {
        return LIST + "?table=" + table.getSlug();
    }
}
