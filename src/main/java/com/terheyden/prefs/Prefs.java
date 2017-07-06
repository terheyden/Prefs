package com.terheyden.prefs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.terheyden.prefs.util.AnnotationFinder;
import com.terheyden.prefs.util.AnnotationFinder.AnnotationResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The Java Preferences API allows us to store stuff without worrying about the
 * disk / location / OS. However it shouldn't be used for large files.
 *
 * See: AppSettings class in test dir for an example
 * See: https://docs.oracle.com/javase/8/docs/technotes/guides/preferences/overview.html
 */
public enum Prefs {
    ;

    // Note that Java Preferences are stored in the registry on Windows, and in ~/Library/* on Mac.

    // Preference maps for the isGlobal and user spaces.
    // Key is the consumer's path, e.g., "/com/myname/myapp".
    private static final Map<String, Preferences> userPrefMap = new HashMap<>();
    private static final Map<String, Preferences> sysPrefMap = new HashMap<>();

    // Supported serialized types:
    private static final Type TYPE_HASH_SET_STR = new TypeToken<HashSet<String>>() { }.getType();
    private static final Type TYPE_ARRAY_LIST_STR = new TypeToken<ArrayList<String>>() { }.getType();
    private static final Type TYPE_LINKED_LIST_STR = new TypeToken<LinkedList<String>>() { }.getType();
    private static final Type TYPE_HASH_MAP_STR_STR = new TypeToken<HashMap<String, String>>() { }.getType();

    /**
     * For working with objs. Don't use this, use the gson() method instead.
     */
    private static Gson _gson;

    /**
     * Lazy lookup of the {@link Preferences} obj corresponding to the user / sys domain
     * and the specified user path. Creates if not found.
     *
     * @param sysPrefs 'user' space, or 'isGlobal' space?
     */
    private static Preferences getPrefs(boolean sysPrefs, String prefPath) {

        Map<String, Preferences> prefMap = sysPrefs ? sysPrefMap : userPrefMap;

        if (!prefMap.containsKey(prefPath)) {
            Preferences prefs = sysPrefs ? Preferences.systemRoot().node(prefPath) : Preferences.userRoot().node(prefPath);
            prefMap.put(prefPath, prefs);
        }

        return prefMap.get(prefPath);
    }

    /**
     * CANNOT BE NULL.
     */
    private static void setStr(Preferences prefs, String key, String val) {
        prefs.put(key, val);
    }

    private static void setInt(Preferences prefs, String key, int val) {
        prefs.putInt(key, val);
    }

    private static void setLong(Preferences prefs, String key, long val) {
        prefs.putLong(key, val);
    }

    private static void setBool(Preferences prefs, String key, boolean val) {
        prefs.putBoolean(key, val);
    }

    private static void setJsonObj(Preferences prefStore, String key, Object val) {
        prefStore.put(key, gson().toJson(val));
    }

    private static void setLinkedList(Preferences prefs, String key, LinkedList<String> val) {
        setJsonObj(prefs, key, val);
    }

    private static void setArrayList(Preferences prefs, String key, ArrayList<String> val) {
        setJsonObj(prefs, key, val);
    }

    private static void setHashMap(Preferences prefs, String key, HashMap<String, String> val) {
        setJsonObj(prefs, key, val);
    }

    private static void setHashSet(Preferences prefs, String key, HashSet<String> val) {
        setJsonObj(prefs, key, val);
    }

    /**
     * Use sparingly, throws if there are no values.
     */
    public static void flushAllPrefs() {
        try {

            for (String key : userPrefMap.keySet()) {
                userPrefMap.get(key).flush();
            }

            for (String key : sysPrefMap.keySet()) {
                sysPrefMap.get(key).flush();
            }

        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    private static String getStr(Preferences prefs, String key, String defaultVal) {
        return prefs.get(key, defaultVal);
    }

    private static int getInt(Preferences prefs, String key, int defaultVal) {
        return prefs.getInt(key, defaultVal);
    }

    private static long getLong(Preferences prefs, String key, long defaultVal) {
        return prefs.getLong(key, defaultVal);
    }

    private static boolean getBool(Preferences prefs, String key, boolean defaultVal) {
        return prefs.getBoolean(key, defaultVal);
    }

    private static <T> T getJsonObj(Preferences prefStore, String key, T defaultVal, Type valType) {

        String json = prefStore.get(key, null);
        if (json == null) {
            return defaultVal;
        }

        return gson().fromJson(json, valType);
    }

    private static LinkedList<String> getLinkedList(Preferences prefs, String key, LinkedList<String> defaultVal) {
        return getJsonObj(prefs, key, defaultVal, TYPE_LINKED_LIST_STR);
    }

    private static ArrayList<String> getArrayList(Preferences prefs, String key, ArrayList<String> defaultVal) {
        return getJsonObj(prefs, key, defaultVal, TYPE_ARRAY_LIST_STR);
    }

    private static HashMap<String, String> getHashMap(Preferences prefs, String key, HashMap<String, String> defaultVal) {
        return getJsonObj(prefs, key, defaultVal, TYPE_HASH_MAP_STR_STR);
    }

    private static HashSet<String> getHashSet(Preferences prefs, String key, HashSet<String> defaultVal) {
        return getJsonObj(prefs, key, defaultVal, TYPE_HASH_SET_STR);
    }

    /**
     * Delete all isGlobal and user preferences!
     */
    public static void deleteAllPrefs() {

        userPrefMap.values().forEach(prefs -> {
            try {

                Arrays.stream(prefs.keys()).forEach(prefs::remove);

            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
        });

        sysPrefMap.values().forEach(prefs -> {
            try {

                Arrays.stream(prefs.keys()).forEach(prefs::remove);

            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Debug the prefs state.
     */
    public static void dump() {
        try {

            sysPrefMap.forEach((path, prefs) -> {
                try {

                    System.out.println("SYSTEM PREFS: " + path);
                    Arrays.stream(prefs.keys()).forEach(k -> {
                        // Values can't be null, so should never see (?) output..
                        System.out.println(String.format("- %-20s = %s", k, prefs.get(k, "(?)")));
                    });

                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }
            });

            userPrefMap.forEach((path, prefs) -> {
                try {

                    System.out.println("USER PREFS: " + path);
                    Arrays.stream(prefs.keys()).forEach(k -> {
                        // Values can't be null, so should never see (?) output..
                        System.out.println(String.format("- %-20s = %s", k, prefs.get(k, "(?)")));
                    });

                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void walkObjectPrefs(Object bindObj, PrefWalker prefWalker) {

        if (bindObj == null) {
            throw new IllegalArgumentException("You can't save a null obj.");
        }

        // Determine the prefPath from the object's package, and @PrefSettings.
        String prefPath = getPrefPath(bindObj);

        // Look up all fields annotated with @Pref.
        List<AnnotationResult<Field>> bindFields = AnnotationFinder.findAnnotatedFields(bindObj, Pref.class);

        for (AnnotationResult<Field> bindField : bindFields) {
            try {

                Field f = bindField.element;
                Object obj = bindField.obj;
                Class<?> type = f.getType();
                Pref ann = (Pref) bindField.annotation;
                String key = With.str(ann.name()).ifBlank(f.getName());
                boolean useSys = ann.isGlobal();
                Preferences pref = getPrefs(useSys, prefPath);
                String defaultVal = ann.defaultVal();

                // Force accessible:
                boolean wasAccessible = f.isAccessible();

                if (!wasAccessible) {
                    f.setAccessible(true);
                }

                try {

                    prefWalker.walkPref(pref, f, type, obj, key, defaultVal);

                } finally {
                    if (!wasAccessible) {
                        f.setAccessible(false);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } // end for each bound field found.
    }

    @FunctionalInterface
    private interface PrefWalker {

        void walkPref(
            Preferences prefs,
            Field annField,
            Class<?> annFieldType,
            Object annObj,
            String prefKeyName,
            String defaultVal
        ) throws Exception;
    }

    /**
     * Save the object's state into Java {@link Preferences}.
     * Only saves fields that are annotated with {@link Pref} or {@link SysPref}.
     * See also: {@link PrefSettings}, {@link #load(Object)}.
     * @param bindObj almost always 'this'
     */
    public static void save(Object bindObj) {

        walkObjectPrefs(bindObj, (prefs, annField, annFieldType, annObj, prefKeyName, defaultVal) -> {

            // Deal with a null value:

            boolean isNull = annField.get(annObj) == null;

            if (isNull) {

                boolean hasOldVal = prefs.get(prefKeyName, null) != null;

                if (hasOldVal) {
                    prefs.remove(prefKeyName);
                }

            } else {

                // Only try to save non-null values.

                if (annFieldType == String.class) {
                    setStr(prefs, prefKeyName, (String) annField.get(annObj));
                } else if (annFieldType == Integer.class || annFieldType == Integer.TYPE) {
                    setInt(prefs, prefKeyName, annField.getInt(annObj));
                } else if (annFieldType == Long.class || annFieldType == Long.TYPE) {
                    setLong(prefs, prefKeyName, annField.getLong(annObj));
                } else if (annFieldType == Boolean.class || annFieldType == Boolean.TYPE) {
                    setBool(prefs, prefKeyName, annField.getBoolean(annObj));
                } else if (annFieldType == LinkedList.class) {
                    setLinkedList(prefs, prefKeyName, (LinkedList<String>) annField.get(annObj));
                } else if (annFieldType == ArrayList.class || annFieldType == List.class) {
                    setArrayList(prefs, prefKeyName, (ArrayList<String>) annField.get(annObj));
                } else if (annFieldType == HashMap.class || annFieldType == Map.class) {
                    setHashMap(prefs, prefKeyName, (HashMap<String, String>) annField.get(annObj));
                } else if (annFieldType == HashSet.class || annFieldType == Set.class) {
                    setHashSet(prefs, prefKeyName, (HashSet<String>) annField.get(annObj));
                } else {
                    throw new IllegalArgumentException("Unknown type: " + annFieldType.getName());
                }

            }

        });
    }

    /**
     * Load the object's state from Java {@link Preferences}.
     * Only loads fields that are annotated with {@link Pref} or {@link SysPref}.
     * See also: {@link PrefSettings}, {@link #save(Object)}.
     * @param bindObj almost always 'this'
     */
    public static void load(Object bindObj) {

        walkObjectPrefs(bindObj, (prefs, annField, annFieldType, annObj, prefKeyName, defaultVal) -> {

            boolean isSaved = getStr(prefs, prefKeyName, null) != null;

            // Don't mess with this field's existing setup if there's no saved value.
            if (!isSaved) {
                return;
            }

            if (annFieldType == String.class) {
                annField.set(annObj, getStr(prefs, prefKeyName, defaultVal));
            } else if (annFieldType == Integer.class || annFieldType == Integer.TYPE) {
                annField.setInt(annObj, getInt(prefs, prefKeyName, defaultVal.isEmpty() ? 0 : Integer.parseInt(defaultVal)));
            } else if (annFieldType == Long.class || annFieldType == Long.TYPE) {
                annField.setLong(annObj, getLong(prefs, prefKeyName, defaultVal.isEmpty() ? 0 : Long.parseLong(defaultVal)));
            } else if (annFieldType == Boolean.class || annFieldType == Boolean.TYPE) {
                boolean defaultBool = defaultVal.isEmpty() ? false : Boolean.parseBoolean(defaultVal);
                annField.setBoolean(annObj, getBool(prefs, prefKeyName, defaultBool));
            } else if (annFieldType == LinkedList.class) {
                annField.set(annObj, getLinkedList(prefs, prefKeyName, null));
            } else if (annFieldType == ArrayList.class || annFieldType == List.class) {
                annField.set(annObj, getArrayList(prefs, prefKeyName, null));
            } else if (annFieldType == HashMap.class || annFieldType == Map.class) {
                annField.set(annObj, getHashMap(prefs, prefKeyName, null));
            } else if (annFieldType == HashSet.class || annFieldType == Set.class) {
                annField.set(annObj, getHashSet(prefs, prefKeyName, null));
            } else {
                throw new IllegalArgumentException("Not sure how to unbind: " + annFieldType);
            }
        });
    }

    /**
     * Determine the prefs path from the object's package or @PrefSettings annotation.
     * Will throw if we can't determine a legal prefs path.
     */
    private static String getPrefPath(Object obj) {

        // Check for a custom path.
        List<Annotation> customNames = AnnotationFinder.findAnnotatedClass(obj, PrefSettings.class);

        if (!customNames.isEmpty()) {

            String path = ((PrefSettings) customNames.get(0)).path();
            if (!path.isEmpty()) {

                if (!path.startsWith("/")) {
                    throw new IllegalArgumentException("Invalid PrefSettings path - must begin with a forward slash: " + path);
                }

                if (path.contains(".")) {
                    throw new IllegalArgumentException("Invalid PrefSettings path - should contain forward slashes, not dots: " + path);
                }

                return path;
            }
        }

        // No custom name, so use the package.
        String pkg = obj.getClass().getPackage().getName();

        if (pkg == null || pkg.isEmpty()) {
            throw new IllegalStateException("Invalid Prefs path - if you want to use Prefs on a class in the default package, please specify a path via the @PrefSettings annotation, thanks! BTW I'm referring to the class: " + obj.getClass().getName());
        }

        if (!pkg.contains(".")) {
            throw new IllegalStateException("Invalid Prefs path - your class package is really short. Please specify an appropriate prefs path via the @PrefSettings annotation, thanks! BTW I'm referring to the class: " + obj.getClass().getName());
        }

        // Reformat into a path and return.
        return "/" + pkg.replaceAll("\\.", "/");
    }

    /**
     * Lazy loaded compact {@link Gson} client for working with collections and stuff.
     */
    private static Gson gson() {
        if (_gson == null) {
            // This is the compact form, since size matters in the Preferences store.
            _gson = new Gson();
        }

        return _gson;
    }
}

/*

Mac dirs:

    FYI your application may write to the following directories:
        ~/Library/Application Support/<app-identifier>
        ~/Library/<app-identifier>
        ~/Library/Caches/<app-identifier>

    See: https://developer.apple.com/library/content/documentation/General/Conceptual/MOSXAppProgrammingGuide/AppRuntime/AppRuntime.html

 */
