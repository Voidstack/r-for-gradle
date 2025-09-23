\# R for Gradle



Plugin Gradle pour générer automatiquement une classe R.java avec des constantes pour tous vos fichiers de ressources, similaire à la classe R.java d'Android.



\## Installation



\### Option 1: Plugin DSL (Recommandé)



```gradle

plugins {

&nbsp;   id 'com.enosistudio.r-for-gradle' version '1.0.2'

}

```



\### Option 2: Legacy Plugin Application



```gradle

buildscript {

&nbsp;   repositories {

&nbsp;       gradlePluginPortal()

&nbsp;   }

&nbsp;   dependencies {

&nbsp;       classpath 'com.enosistudio:r-for-gradle:1.0.2'

&nbsp;   }

}



apply plugin: 'com.enosistudio.r-for-gradle'

```



\## Configuration



Vous pouvez configurer le plugin dans votre `build.gradle` :



```gradle

generateR {

&nbsp;   // Garder les fichiers générés dans src/main/java au lieu de build/generated

&nbsp;   keepInProjectFiles = true // défaut: true

&nbsp;   

&nbsp;   // Répertoire des ressources à scanner

&nbsp;   resourcesDir = file('src/main/resources') // défaut

&nbsp;   

&nbsp;   // Package de la classe R générée

&nbsp;   packageName = 'com.votre.package.generated' // défaut: com.enosistudio.generated

&nbsp;   

&nbsp;   // Répertoire de sortie pour les sources générées (si keepInProjectFiles = false)

&nbsp;   outputTargetDirectory = file('build/generated/sources/r')

&nbsp;   

&nbsp;   // Répertoire de sortie pour les sources du projet (si keepInProjectFiles = true)

&nbsp;   outputSrcDirectory = file('src/main/java')

}

```



\## Utilisation



\### Génération automatique



Le plugin s'exécute automatiquement avant la compilation Java. Vous pouvez aussi l'exécuter manuellement :



```bash

./gradlew generateR

```



\### Exemple d'utilisation



Si vous avez cette structure de ressources :



```

src/main/resources/

├── config.properties

├── templates/

│   ├── email.html

│   └── report.pdf

└── images/

&nbsp;   ├── logo.png

&nbsp;   └── icons/

&nbsp;       └── home.png

```



La classe R générée ressemblera à :



```java

package com.enosistudio.generated;



public final class R {

&nbsp;   public static final RFile configProperties = new RFile("config.properties");

&nbsp;   

&nbsp;   public static final class Templates extends RFolder {

&nbsp;       public static final RFolder \_self = new Templates();

&nbsp;       private Templates() { super("templates", "templates"); }

&nbsp;       

&nbsp;       public static final RFile emailHtml = new RFile("templates/email.html");

&nbsp;       public static final RFile reportPdf = new RFile("templates/report.pdf");

&nbsp;   }

&nbsp;   

&nbsp;   public static final class Images extends RFolder {

&nbsp;       public static final RFolder \_self = new Images();

&nbsp;       private Images() { super("images", "images"); }

&nbsp;       

&nbsp;       public static final RFile logoPng = new RFile("images/logo.png");

&nbsp;       

&nbsp;       public static final class Icons extends RFolder {

&nbsp;           public static final RFolder \_self = new Icons();

&nbsp;           private Icons() { super("icons", "images/icons"); }

&nbsp;           

&nbsp;           public static final RFile homePng = new RFile("images/icons/home.png");

&nbsp;       }

&nbsp;   }

}

```



\### Dans votre code Java



```java

// Accéder à un fichier

InputStream configStream = R.configProperties.getInputStream();

String configPath = R.configProperties.getPath(); // "config.properties"



// Accéder à un fichier dans un sous-dossier

InputStream logoStream = R.Images.logoPng.getInputStream();

URL logoUrl = R.Images.logoPng.getURL();



// Accéder à un dossier

String templatesPath = R.Templates.\_self.getPath(); // "templates"

File templatesDir = R.Templates.\_self.getFile();

```



\## Structure du projet



```

src/

├── main/

│   └── java/

│       └── com/

│           └── enosistudio/

│               ├── GenerateRPlugin.java

│               ├── GenerateRTask.java

│               └── GenerateRExtension.java

└── test/

&nbsp;   └── java/

&nbsp;       └── com/

&nbsp;           └── enosistudio/

&nbsp;               └── GenerateRPluginTest.java

```



\## Compatibilité



\- Java 17+

\- Gradle 7.0+



\## Différences avec la version Maven



1\. \*\*Configuration\*\* : Utilise une extension Gradle au lieu de paramètres Maven

2\. \*\*Tâches\*\* : Intégration avec le système de tâches Gradle

3\. \*\*Dépendances\*\* : Utilise l'API Gradle au lieu de l'API Maven

4\. \*\*Publication\*\* : Configuration pour Gradle Plugin Portal



\## Développement



\### Test local



```bash

./gradlew publishToMavenLocal

```



Puis dans un autre projet :



```gradle

buildscript {

&nbsp;   repositories {

&nbsp;       mavenLocal()

&nbsp;       gradlePluginPortal()

&nbsp;   }

&nbsp;   dependencies {

&nbsp;       classpath 'com.enosistudio:r-for-gradle:1.0.2'

&nbsp;   }

}



apply plugin: 'com.enosistudio.r-for-gradle'

```



\### Publication



Pour publier sur Gradle Plugin Portal :



```bash

./gradlew publishPlugins

```

