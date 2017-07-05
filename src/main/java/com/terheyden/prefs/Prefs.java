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

    // Preference maps for the system and user spaces.
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
     * Used for iterating over all prefs...
     */
    private static final Class<?>[] classes = { UserPref.class, SysPref.class };


    /**
     * Lazy lookup of the {@link Preferences} obj corresponding to the user / sys domain
     * and the specified user path. Creates if not found.
     *
     * @param userPrefs 'user' space, or 'system' space?
     */
    private static Preferences getPrefs(boolean userPrefs, String prefPath) {

        Map<String, Preferences> prefMap = userPrefs ? userPrefMap : sysPrefMap;

        if (!prefMap.containsKey(prefPath)) {
            Preferences prefs = userPrefs ? Preferences.userRoot().node(prefPath) : Preferences.systemRoot().node(prefPath);
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
     * Delete all system and user preferences!
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

    /**
     * Save the object's state into Java {@link Preferences}.
     * Only saves fields that are annotated with {@link UserPref} or {@link SysPref}.
     * See also: {@link PrefSettings}, {@link #bindLoad(Object)}.
     * @param bindObj almost always 'this'
     */
    public static void bindSave(Object bindObj) {

        if (bindObj == null) {
            throw new IllegalArgumentException("You can't bind save a null obj.");
        }

        // Determine the prefPath from the object.
        String prefPath = getPrefPath(bindObj);
        Preferences[] prefs = { getPrefs(true, prefPath), getPrefs(false, prefPath) };

        // In this loop, we process user prefs, then sys prefs:

        for (int i = 0; i < 2; i++) {

            Class<?> annClass = classes[i];
            Preferences pref = prefs[i];

            List<AnnotationResult<Field>> bindFields = AnnotationFinder.findAnnotatedFields(bindObj, annClass);

            for (AnnotationResult<Field> bindField : bindFields) {
                try {

                    Field f = bindField.element;
                    Object obj = bindField.obj;
                    Class<?> type = f.getType();
                    String key = getFieldKey(f, annClass, bindField.annotation);

                    // Force accessible:
                    boolean wasAccessible = f.isAccessible();

                    if (!wasAccessible) {
                        f.setAccessible(true);
                    }

                    // Deal with a null value:

                    boolean isNull = f.get(obj) == null;

                    if (isNull) {

                        boolean hasOldVal = pref.get(key, null) != null;

                        if (hasOldVal) {
                            pref.remove(key);
                        }

                    } else {

                        // Only try to save non-null values.

                        try {
                            if (type == String.class) {
                                setStr(pref, key, (String) f.get(obj));
                            } else if (type == Integer.class || type == Integer.TYPE) {
                                setInt(pref, key, f.getInt(obj));
                            } else if (type == Long.class || type == Long.TYPE) {
                                setLong(pref, key, f.getLong(obj));
                            } else if (type == Boolean.class || type == Boolean.TYPE) {
                                setBool(pref, key, f.getBoolean(obj));
                            } else if (type == LinkedList.class) {
                                setLinkedList(pref, key, (LinkedList<String>) f.get(obj));
                            } else if (type == ArrayList.class || type == List.class) {
                                setArrayList(pref, key, (ArrayList<String>) f.get(obj));
                            } else if (type == HashMap.class || type == Map.class) {
                                setHashMap(pref, key, (HashMap<String, String>) f.get(obj));
                            } else if (type == HashSet.class || type == Set.class) {
                                setHashSet(pref, key, (HashSet<String>) f.get(obj));
                            } else {
                                throw new IllegalArgumentException("Unknown type: " + type.getName());
                            }

                        } finally {
                            if (!wasAccessible) {
                                f.setAccessible(false);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } // end looping over user and sys.
    }

    /**
     * Load the object's state from Java {@link Preferences}.
     * Only loads fields that are annotated with {@link UserPref} or {@link SysPref}.
     * See also: {@link PrefSettings}, {@link #bindSave(Object)}.
     * @param bindObj almost always 'this'
     */
    public static void bindLoad(Object bindObj) {

        if (bindObj == null) {
            throw new IllegalArgumentException("You can't bind save a null obj.");
        }

        // Determine the prefPath from the object.
        String prefPath = getPrefPath(bindObj);
        Preferences[] prefs = { getPrefs(true, prefPath), getPrefs(false, prefPath) };

        // In this loop, we process user prefs, then sys prefs:

        for (int i = 0; i < 2; i++) {

            Class<?> annClass = classes[i];
            Preferences pref = prefs[i];

            List<AnnotationResult<Field>> bindFields = AnnotationFinder.findAnnotatedFields(bindObj, annClass);

            for (AnnotationResult<Field> bindField : bindFields) {
                try {

                    Field f = bindField.element;
                    Class<?> type = f.getType();
                    Object obj = bindField.obj;
                    String key = getFieldKey(f, annClass, bindField.annotation);
                    String defVal = getFieldDefaultValue(f, annClass, bindField.annotation);

                    // If they leave the bind key blank, we default to the field name.
                    if (key.isEmpty()) {
                        key = f.getName();
                    }

                    // Force accessible:
                    boolean wasAccessible = f.isAccessible();

                    if (!wasAccessible) {
                        f.setAccessible(true);
                    }

                    boolean isSaved = getStr(pref, key, null) != null;

                    // Don't mess with the field if there's no saved value.
                    if (isSaved) {
                        try {
                            if (type == String.class) {
                                f.set(obj, getStr(pref, key, defVal));
                            } else if (type == Integer.class || type == Integer.TYPE) {
                                f.setInt(obj, getInt(pref, key, defVal.isEmpty() ? 0 : Integer.parseInt(defVal)));
                            } else if (type == Long.class || type == Long.TYPE) {
                                f.setLong(obj, getLong(pref, key, defVal.isEmpty() ? 0 : Long.parseLong(defVal)));
                            } else if (type == Boolean.class || type == Boolean.TYPE) {
                                f.setBoolean(obj, getBool(pref, key, defVal.isEmpty() ? false : Boolean.parseBoolean(defVal)));
                            } else if (type == LinkedList.class) {
                                f.set(obj, getLinkedList(pref, key, null));
                            } else if (type == ArrayList.class || type == List.class) {
                                f.set(obj, getArrayList(pref, key, null));
                            } else if (type == HashMap.class || type == Map.class) {
                                f.set(obj, getHashMap(pref, key, null));
                            } else if (type == HashSet.class || type == Set.class) {
                                f.set(obj, getHashSet(pref, key, null));
                            } else {
                                throw new IllegalArgumentException("Not sure how to unbind: " + type);
                            }

                        } finally {
                            if (!wasAccessible) {
                                f.setAccessible(false);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } // end for user, then sys prefs

    }

    /**
     * Get the @UserPref or @SysPref key value from an annotated class field.
     */
    private static String getFieldKey(Field annField, Class<?> annClass, Annotation ann) {

        // Get the key the user set inside the annotation.

        String key = null;

        if (annClass == UserPref.class) {
            key = ((UserPref) ann).key();
        } else if (annClass == SysPref.class) {
            key = ((SysPref) ann).key();
        }

        // The default is "". If they didn't set the key name, default to the field name.
        if (key == null || key.isEmpty()) {
            key = annField.getName();
        }

        return key;
    }

    /**
     * Get the @UserPref or @SysPref default value from an annotated class field.
     */
    private static String getFieldDefaultValue(Field field, Class<?> annClass, Annotation ann) {

        if (annClass == UserPref.class) {
            return ((UserPref) ann).defaultVal();
        } else if (annClass == SysPref.class) {
            return ((SysPref) ann).defaultVal();
        }

        throw new IllegalArgumentException("Unknown type: " + annClass.getName());
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
