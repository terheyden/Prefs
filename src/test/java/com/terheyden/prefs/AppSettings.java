package com.terheyden.prefs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Example system / user preferences usage with my annotations.
 * See: {@link Prefs}
 */
public class AppSettings {

    private static final Gson gson = new Gson();

    @SysPref(key = "license", defaultVal = "UNLICENSED")
    public String sysLicense;

    @UserPref
    public String lastDir;

    @UserPref
    public int timesRan;

    @UserPref(key = "saveOnExit", defaultVal = "true")
    public boolean saveOnExit;

    // Lists, maps, and sets of Strings are supported.
    // Use an explicit type, e.g. LinkedList or ArrayList, for best results.
    // Private fields are also supported.

    @SysPref
    private Set<String> cache = new HashSet<>();

    public Set<String> getCache() {
        return cache;
    }

    @SysPref
    public Map<String, String> map;

    ////////////////////////////////////////////////////////////////////////////////
    // Below is an example of how to work with other types of complex data:

    private static final Type TYPE_SET_USER = new TypeToken<Set<User>>() { }.getType();

    @UserPref
    private String usersData;
    public Set<User> users;

    public void save() {

        // Convert objs to strings.
        usersData = gson.toJson(users);

        Prefs.bindSave(this);
    }

    public void load() {

        Prefs.bindLoad(this);

        users = gson.fromJson(usersData, TYPE_SET_USER);
    }
}
