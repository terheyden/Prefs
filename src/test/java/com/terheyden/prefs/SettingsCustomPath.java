package com.terheyden.prefs;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * For testing {@link Prefs}.
 */
@PrefSettings(path = "/com/terheyden/javautils")
public class SettingsCustomPath {

    @Pref
    public ArrayList<String> alist = new ArrayList<>();

    @Pref
    public LinkedList<String> llist = new LinkedList<>();

    public void save() {
        Prefs.save(this);
    }

    public void load() {
        Prefs.load(this);
    }
}
