package com.hopebridge.model.factory;

import com.hopebridge.model.exception.ValidationException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The lookup table every factory uses: "for this code, here is how to build the object".
 *
 * Instead of a growing if/else in each factory, the factories just register one line
 * per type. Adding a new type means adding one more register(...) call, nothing else.
 */

final class TypeRegistry<T> {

    private final String kind;
    private final Map<String, Supplier<? extends T>> creators = new LinkedHashMap<>();

    TypeRegistry(String kind) {
        this.kind = kind;
    }

    TypeRegistry<T> register(String code, Supplier<? extends T> creator) {
        creators.put(code, creator);
        return this;
    }

    // Codes come from the database or from a form, so we normalise them before looking them up.
    T create(String code) {
        String key = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
        Supplier<? extends T> creator = creators.get(key);
        if (creator == null) {
            throw new ValidationException("Unknown " + kind + ": " + code);
        }
        return creator.get();
    }

    // One fresh object of every registered type, in registration order (handy for building option lists).
    List<T> createAll() {
        return creators.values().stream().<T>map(Supplier::get).toList();
    }
}
