package com.terheyden.prefs;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * For testing {@link Prefs}.
 */
@PrefSettings(path = "/com/terheyden/javautils")
public class SettingsCustomPath {

    @UserPref
    public ArrayList<String> alist = new ArrayList<>();

    @SysPref
    public LinkedList<String> llist = new LinkedList<>();

    public void save() {
        Prefs.bindSave(this);
    }

    public void load() {
        Prefs.bindLoad(this);
    }
}
