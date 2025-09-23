package com.enosistudio;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

/**
 * Extension class for configuring the GenerateR plugin.
 */
public class GenerateRExtension {
    private final Property<Boolean> keepInProjectFiles;
    private final DirectoryProperty resourcesDir;
    private final Property<String> packageName;
    private final DirectoryProperty outputTargetDirectory;
    private final DirectoryProperty outputSrcDirectory;

    @Inject
    public GenerateRExtension(ObjectFactory objectFactory) {
        this.keepInProjectFiles = objectFactory.property(Boolean.class);
        this.resourcesDir = objectFactory.directoryProperty();
        this.packageName = objectFactory.property(String.class);
        this.outputTargetDirectory = objectFactory.directoryProperty();
        this.outputSrcDirectory = objectFactory.directoryProperty();

        // Set defaults
        this.keepInProjectFiles.convention(true);
        this.packageName.convention("com.enosistudio.generated");
    }

    public Property<Boolean> getKeepInProjectFiles() {
        return keepInProjectFiles;
    }

    public DirectoryProperty getResourcesDir() {
        return resourcesDir;
    }

    public Property<String> getPackageName() {
        return packageName;
    }

    public DirectoryProperty getOutputTargetDirectory() {
        return outputTargetDirectory;
    }

    public DirectoryProperty getOutputSrcDirectory() {
        return outputSrcDirectory;
    }
}