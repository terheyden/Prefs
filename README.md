# Prefs
A better way to work with Java Preferences.

## About
[Java Preferences](http://docs.oracle.com/javase/7/docs/technotes/guides/preferences/overview.html) are a part of core Java. They provide an abstracted, cross-platform way to store preference and config data for your application.

`Prefs` is a tiny library that wraps Java Preferences to make them easier to use.
It replaces a bunch of `save()` and `load()` calls with some simple annotations.
Let's take a look at an example.

## Simplest example
Here's a simplified example Java class where we'll put our app settings:

```$java
package com.example.myapp;

import com.terheyden.prefs.Prefs;
import com.terheyden.prefs.Pref;

public class AppSettings {

    @Pref                                       // Mark this field to be saved.
    private String lastDir;

    public String getLastDir() {
        return lastDir;
    }

    public void setLastDir(String lastDir) {
        this.lastDir = lastDir;
    }

    public void save() {
        Prefs.save(this);               // Call save() to save this object's @Pref fields.
    }

    public void load() {
        Prefs.load(this);               // Call load() to load.
    }
}
```

Above outlines the simplest possible usage.
We have one field we want to save, and a save() and load() method.
An example usage might be:

```java
    AppSettings settings = new AppSettings();
    settings.load();

    // .. do some stuff ..
    String dir = "/";
    settings.setLastDir(dir);

    // .. ready to exit ..
    settings.save();
```

## Complex example
Here's a more complete reference example:

```java
package com.example.myapp;

import com.terheyden.prefs.PrefSettings;
import com.terheyden.prefs.Prefs;
import com.terheyden.prefs.Pref;

@PrefSettings(path = "/com/example/myproject/myapp")
public class AppSettings {

    @Pref(isGlobal = true, name = "lastDirUsed", defaultVal = "/")
    private String lastDir;

    public AppSettings() {
        Prefs.load(this);
    }

    public String getLastDir() {
        return lastDir;
    }

    public void setLastDir(String lastDir) {
        this.lastDir = lastDir;
        Prefs.save(this);
    }
}
```

Let's start at the top, and look at the `Prefs`-specific settings:

`@PrefSettings`:
- a class-level annotation, lets you specify settings for `Prefs`
- `path` tells `Prefs` where to store your preferences in the registry. By default, the path is determined by the class's package, but you may want to organize your preferences differently

`@Pref`:
- `isGlobal` = save this preference in the system space, instead of with the user
- `name` = the name to store this preference as in the registry. By default, the field's name is used
- `defaultVal` = when this field has no value, the `defaultVal` will be used instead

Notice also in this example that we call `load()` in the constructor and `save()` automatically when `setLastDir()` is called.
You're free to set these up however you see fit.

## Supported types ##

Here are the field types you can bind, at the moment:
- String
- int
- boolean
- List&lt;String&gt;, ArrayList&lt;String&gt;, LinkedList&lt;String&gt;
- Map&lt;String&gt;, HashMap&lt;String&gt;
- Set&lt;String&gt;, HashSet&lt;String&gt;

It is pretty trivial to add more, but that's what works right now.
Also, example code is provided that demonstrates how to save complex types.
