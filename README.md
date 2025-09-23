# 🚀 R for Gradle

[![Maven Central](https://img.shields.io/maven-central/v/com.enosistudio/r-for-gradle.svg)](https://central.sonatype.com/artifact/com.enosistudio/r-for-gradle)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-11%2B-brightgreen.svg)](https://openjdk.java.net/)

> **Type-safe hierarchical resource access for Java Gradle projects - inspired by Android's R.java!**

Generate a type-safe `R.java` class that mirrors your resource directory structure. Access files and folders with intuitive syntax like `R.config.database.readContent()` while enjoying full IDE autocompletion and compile-time safety.

---

## ✨ Features

* 📁 **Hierarchical Structure** – Mirrors your `src/main/resources` directory
* 🏗️ **Gradle Integration** – Generates during Gradle build
* 🔤 **Smart Naming** – Converts file/folder names to camelCase Java identifiers
* 📖 **Rich File API** – Read, stream, and manipulate paths easily
* 📂 **Folder Methods** – Access folder metadata with `_self.getName()` and `_self.getPath()`
* ⚡ **Fast Generation** – Lightweight and efficient

---

## 📦 Installation

Add the plugin to your `build.gradle`:

```gradle
plugins {
    id "com.enosistudio.r-for-gradle" version "1.0.2"
}

dependencies {
    implementation "com.enosistudio:r-for-gradle:1.0.2"
}

generateR{
  keepInProjectFiles = false // Optional: Keep generated R files in the project directory or not
  // Other optionnal conf ...
}
```

---

## 🏃‍♂️ Usage

### Before (❌ Error-prone)

```java
// Hardcoded strings everywhere!
InputStream config = getClass().getResourceAsStream("/config/database.properties");
InputStream logo = getClass().getResourceAsStream("/images/icons/logo.png");

// Typos cause runtime errors 💥
String content = Files.readString(Paths.get("config/databse.properties")); // Whoops!
```

### After (✅ Type-safe & Intuitive)

```java
import com.enosistudio.generated.R;

// Hierarchical access with autocompletion!
String content = R.config.databaseProperties.readContent();
InputStream logo = R.images.icons.logoPng.openStream();
URL resource = R.templates.emailHtml.getURL();

// Folder information
String folderName = R.config._self.getName();     // "config"
String folderPath = R.config._self.getPath();     // "config"

// Compile-time safety 🛡️
R.config.databseProperties.readContent(); // Won't compile - typo caught!
```

---

## 📂 Generated Structure

**Resources:**

```
src/main/resources/
├── config/
│   ├── database.properties
│   └── app-settings.yml
├── templates/
│   ├── email.html
│   └── reports/
│       └── invoice.pdf
└── logo.png
```

**Generated R.java:**

```java
package com.enosistudio.generated;

public final class R {
    public static final RFile logoPng = new RFile("logo.png");
    
    public static final class config extends RFolder {
        public static final RFolder _self = new config();
        private config() { super("config", "config"); }
        
        public static final RFile databaseProperties = new RFile("config/database.properties");
        public static final RFile appSettingsYml = new RFile("config/app-settings.yml");
    }
    
    public static final class templates extends RFolder {
        public static final RFolder _self = new templates();
        private templates() { super("templates", "templates"); }
        
        public static final RFile emailHtml = new RFile("templates/email.html");
        
        public static final class reports extends RFolder {
            public static final RFolder _self = new reports();
            private reports() { super("reports", "templates/reports"); }
            
            public static final RFile invoicePdf = new RFile("templates/reports/invoice.pdf");
        }
    }
    
    public static class RFolder { /* folder methods */ }
    public static final class RFile { /* rich file API */ }
}
```

---

## ⚙️ Configuration

| Parameter               | Default                     | Description                             |
| ----------------------- | --------------------------- | --------------------------------------- |
| `keepInProjectFiles`    | `true`                      | Keep generated files in `src/main/java` |
| `resourcesDir`          | `src/main/resources`        | Resources directory to scan             |
| `packageName`           | `com.enosistudio.generated` | Package for generated R.java            |
| `outputSrcDirectory`    | `src/main/java`             | Output when `keepInProjectFiles=true`   |
| `outputTargetDirectory` | `build/generated/sources`   | Output when `keepInProjectFiles=false`  |

---

## 🔧 Requirements

* ☕ Java 11+
* 🔨 Gradle 7+

---

⭐ **Star this repo if it helps!**
