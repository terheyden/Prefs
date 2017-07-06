package com.terheyden.prefs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Example system / user Prefs usage.
 * See: {@link Prefs}
 */
public class AppSettings {

    private static final Gson gson = new Gson();

    @Pref(isGlobal = true, name = "license", defaultVal = "UNLICENSED")
    public String sysLicense;

    @Pref
    public String lastDir;

    @Pref(isGlobal = true)
    public int timesRan;

    @Pref(defaultVal = "true")
    public boolean saveOnExit;

    // Lists, maps, and sets of Strings are supported.
    // Use an explicit type, e.g. LinkedList or ArrayList, for best results.
    // Private fields are also supported.

    @Pref
    private Set<String> cache = new HashSet<>();

    public Set<String> getCache() {
        return cache;
    }

    @Pref
    public Map<String, String> map;

    ////////////////////////////////////////////////////////////////////////////////
    // Below is an example of how to work with other types of complex data:

    private static final Type TYPE_SET_USER = new TypeToken<Set<User>>() { }.getType();

    @Pref
    private String usersData;
    public Set<User> users;

    public void save() {

        // Convert objs to strings.
        usersData = gson.toJson(users);

        Prefs.save(this);
    }

    public void load() {

        Prefs.load(this);

        users = gson.fromJson(usersData, TYPE_SET_USER);
    }
}
