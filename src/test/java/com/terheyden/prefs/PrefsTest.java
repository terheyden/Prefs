package com.terheyden.prefs;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

public class PrefsTest {

    @Test
    public void test() {

        // Init:
        AppSettings settings = new AppSettings();
        settings.load();
        Prefs.deleteAllPrefs();

        // Load from nothing should work:
        settings = new AppSettings();
        settings.load();
        Prefs.dump();

        // Seed some data:
        settings.timesRan++;
        settings.getCache().add("cache1");
        settings.getCache().add("cache2");
        settings.map = new HashMap<>();
        settings.map.put("key1", "val1");
        settings.users = new HashSet<>();
        settings.users.add(new User("Mika", 12));
        settings.save();
        Prefs.dump();

        // Load that data:
        AppSettings set2 = new AppSettings();
        set2.load();

        assertEquals(1, set2.timesRan);
        assertEquals(1, set2.users.size());

        for (User user : set2.users) {
            assertEquals("Mika", user.getName());
            assertEquals(12, user.getAge());
        }

        // Add some more data:
        set2.timesRan++;
        set2.users.add(new User("Cora", 6, "Oakland"));
        set2.getCache().add("cache33");
        set2.map.put("key22", "val22");
        set2.save();
        Prefs.dump();

        Prefs.deleteAllPrefs();
    }

    @Test
    public void testCustomPath() {

        SettingsCustomPath settings = new SettingsCustomPath();
        settings.load();

        settings.alist.add("alist1");
        settings.llist.add("llist1");
        settings.save();
        Prefs.dump();

        SettingsCustomPath set2 = new SettingsCustomPath();
        set2.load();
        Prefs.dump();
        Prefs.deleteAllPrefs();
    }
}
