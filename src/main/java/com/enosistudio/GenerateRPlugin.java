package com.enosistudio;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Gradle plugin to generate R.java class with resource constants.
 */
public class GenerateRPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        // Create the extension for configuration
        GenerateRExtension extension = project.getExtensions().create("generateR", GenerateRExtension.class);

        // Register the task
        project.getTasks().register("generateR", GenerateRTask.class, task -> {
            task.setGroup("build");
            task.setDescription("Generate R.java class with resource constants");

            // Configure task with extension values
            task.getResourcesDir().convention(
                    project.getLayout().getProjectDirectory().dir("src/main/resources")
            );
            task.getPackageName().convention("com.enosistudio.generated");
            task.getKeepInProjectFiles().convention(true);
            task.getOutputSrcDirectory().convention(
                    project.getLayout().getProjectDirectory().dir("src/main/java")
            );
            task.getOutputTargetDirectory().convention(
                    project.getLayout().getBuildDirectory().dir("generated/sources/r")
            );
        });

        // Make compileJava depend on generateR
        project.getTasks().named("compileJava", compileTask -> {
            compileTask.dependsOn("generateR");
        });

        // Add generated sources to source sets
        project.afterEvaluate(p -> {
            GenerateRTask generateRTask = (GenerateRTask) p.getTasks().getByName("generateR");
            if (!generateRTask.getKeepInProjectFiles().get()) {
                p.getExtensions().getByType(org.gradle.api.plugins.JavaPluginExtension.class)
                        .getSourceSets().getByName("main")
                        .getJava().srcDir(generateRTask.getOutputTargetDirectory());
            }
        });
    }
}