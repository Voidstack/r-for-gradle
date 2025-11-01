# 🚀 R for Gradle

[![Maven Central](https://img.shields.io/maven-central/v/com.enosistudio/r-for-gradle.svg)](https://central.sonatype.com/artifact/com.enosistudio/r-for-gradle)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-17%2B-brightgreen.svg)](https://openjdk.java.net/)

> **Type-safe hierarchical resource access for Java Gradle projects - inspired by Android's R.java!**

Generate a type-safe `R.java` class that mirrors your resource directory structure. Access files and folders with intuitive syntax like `R.config.database.readContent()` while enjoying full IDE autocompletion and compile-time safety.

---

## ✨ Features

- 📁 **Hierarchical Structure** - Mirrors your resources directory tree
- 🏗️ **Build Integration** - Generates during Maven compilation  
- 🔤 **Smart Naming** - Converts file/folder names to camelCase Java identifiers
- 📂 **Folder Methods** - Access folder methods with `R.myFolder._self`
- ⚡ **Fast Generation** - Lightweight and efficient

---

## 📦 Installation

Add the plugin to your `build.gradle`:

```gradle
plugins {
    id "com.enosistudio.r-for-gradle" version "latest-version"
}

dependencies {
    implementation "com.enosistudio:r-for-gradle:latest-version"
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
String folderName = R.config._self.getName();
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
        private config() { super("config"); }
        
        public static final RFile databaseProperties = new RFile("config/database.properties");
        public static final RFile appSettingsYml = new RFile("config/app-settings.yml");
    }
    
    public static final class templates extends RFolder {
        public static final RFolder _self = new templates();
        private templates() { super("templates"); }
        
        public static final RFile emailHtml = new RFile("templates/email.html");
        
        public static final class reports extends RFolder {
            public static final RFolder _self = new reports();
            private reports() { super("templates/reports"); }
            
            public static final RFile invoicePdf = new RFile("templates/reports/invoice.pdf");
        }
    }
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

* ☕ Java 17+
* 🔨 Gradle 7+

---

## 📙 Other

### Why no `File` or `Path` conversion methods?

Resources should **never** be manipulated as `File` or `Path` objects. Here's why:

- **JAR resources are not files** - They exist as compressed entries within an archive
- **No filesystem access** - Resources in JARs have no valid file path
- **Read-only nature** - Resources cannot be modified at runtime
- **Performance overhead** - Converting to temp files wastes disk space and memory

**Exception:** If a legacy library absolutely requires a `File` object, you must manually copy the resource to a temporary file. However, this should be a last resort, not the default behavior.

For this reason, I don't plan to add any methods related to `java.io.File` or `java.nio.file.Path` objects.

---

## 🤝 Contributing

* ☕ [Buy me a coffee](https://buymeacoffee.com/enosistudio)
* ⭐ If you find this project helpful, please give it a star — it really helps!

---
