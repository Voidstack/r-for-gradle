package com.enosistudio;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.Optional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Gradle task to generate a Java class with constants for resource files and folders.
 * The generated class will be named R and will contain a hierarchical structure
 * for accessing files and folders.
 */
@SuppressWarnings("unused")
public class GenerateRTask extends DefaultTask {
    private static final String PATH_SEPARATOR = "/";

    private final Property<Boolean> keepInProjectFiles = getProject().getObjects().property(Boolean.class);
    private final DirectoryProperty resourcesDir = getProject().getObjects().directoryProperty();
    private final Property<String> packageName = getProject().getObjects().property(String.class);
    private final DirectoryProperty outputTargetDirectory = getProject().getObjects().directoryProperty();
    private final DirectoryProperty outputSrcDirectory = getProject().getObjects().directoryProperty();

    @Input
    public Property<Boolean> getKeepInProjectFiles() {
        return keepInProjectFiles;
    }

    @InputDirectory
    @Optional
    public DirectoryProperty getResourcesDir() {
        return resourcesDir;
    }

    @Input
    public Property<String> getPackageName() {
        return packageName;
    }

    @OutputDirectory
    public DirectoryProperty getOutputTargetDirectory() {
        return outputTargetDirectory;
    }

    @OutputDirectory
    public DirectoryProperty getOutputSrcDirectory() {
        return outputSrcDirectory;
    }

    @TaskAction
    public void generateR() {
        File resourcesDirFile = resourcesDir.get().getAsFile();

        // Choose the base directory
        File baseDir = keepInProjectFiles.get() ? outputSrcDirectory.get().getAsFile() : outputTargetDirectory.get().getAsFile();
        String packagePath = packageName.get().replace('.', '/');
        File outputDir = new File(baseDir, packagePath);

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Failed to create output directory: " + outputDir);
        }

        try {
            generateResourceClasses(outputDir, resourcesDirFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate resource classes", e);
        }

        getLogger().info("Generated R.java with hierarchical resource structure");
    }

    /**
     * Generates the R.java class with hierarchical structure based on the resources' directory.
     * @param outputDir the directory to write the R.java file to
     * @param resourcesDirFile the resources directory to scan
     * @throws IOException if an I/O error occurs
     */
    private void generateResourceClasses(File outputDir, File resourcesDirFile) throws IOException {
        ResourceNode rootNode = buildResourceTree(resourcesDirFile);

        // Generate only the main R class
        generateMainRClass(outputDir, rootNode);
    }

    /**
     * Generates the main R.java class with hierarchical structure.
     * @param outputDir the directory to write the R.java file to
     * @param rootNode the root ResourceNode representing the resources
     * @throws IOException if an I/O error occurs
     */
    private void generateMainRClass(File outputDir, ResourceNode rootNode) throws IOException {
        File rClass = new File(outputDir, "R.java");
        try (FileWriter writer = new FileWriter(rClass)) {
            writer.write(String.format("""
                    package %s;
                    
                    import com.enosistudio.RFile;
                    import com.enosistudio.RFolder;
                    
                    /**
                     * Generated resource constants class.
                     * Contains hierarchical access to all resource files and folders.
                     */
                    @SuppressWarnings({"java:S101", "unused"})
                    public final class R {
                        private R() {} // Utility class
                    
                    """, packageName.get()));

            // Generate static fields for root level items
            generateNodeFields(writer, rootNode, "    ");

            writer.write("}\n");
        }
    }

    /**
     * Recursively generates fields and nested classes for a ResourceNode.
     * @param writer the FileWriter to write the class content to
     * @param node the current ResourceNode
     * @param indent current indentation level
     * @throws IOException if an I/O error occurs
     */
    private void generateNodeFields(FileWriter writer, ResourceNode node, String indent) throws IOException {
        // Generate fields for files at current level
        for (ResourceFile file : node.files) {
            String fieldName = toValidJavaName(file.name);
            writer.write(String.format("%spublic static final RFile %s = new RFile(\"%s\");%n", indent, fieldName, file.path));
        }

        // Generate nested classes for folders
        for (Map.Entry<String, ResourceNode> entry : node.children.entrySet()) {
            String folderName = entry.getKey();
            ResourceNode childNode = entry.getValue();
            String className = toValidJavaName(folderName);

            writer.write(String.format("""
                    %s
                    %spublic static final class %s extends RFolder {
                    %s    public static final RFolder _self = new %s();
                    %s    private %s() { super("%s", "%s"); }
                    """, indent, indent, className, indent, className, indent, className, folderName, childNode.path));

            // Generate nested content (files and subfolders)
            generateNodeFields(writer, childNode, indent + "    ");

            // Close class
            writer.write(String.format("%s}%n", indent));
        }
    }

    /**
     * Builds a tree representation of the resources' directory.
     * @param resourcesDirFile the resources directory
     * @return the root ResourceNode
     */
    private ResourceNode buildResourceTree(File resourcesDirFile) {
        ResourceNode root = new ResourceNode("", "");
        scanResourcesRecursive(resourcesDirFile, "", root);
        return root;
    }

    /**
     * Recursively scans the resources directory and builds a tree of ResourceNode and ResourceFile.
     * @param dir current directory to scan
     * @param currentPath relative path from resources root
     * @param currentNode current ResourceNode to populate
     */
    private void scanResourcesRecursive(File dir, String currentPath, ResourceNode currentNode) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            String filePath = currentPath.isEmpty() ? file.getName() : currentPath + PATH_SEPARATOR + file.getName();

            if (file.isFile()) {
                currentNode.files.add(new ResourceFile(file.getName(), filePath));
            } else if (file.isDirectory()) {
                ResourceNode childNode = new ResourceNode(file.getName(), filePath);
                currentNode.children.put(file.getName(), childNode);
                scanResourcesRecursive(file, filePath, childNode);
            }
        }
    }

    /**
     * Converts a string to a valid Java variable name by replacing invalid characters with underscores
     * and converting to camelCase.
     * @param name the original name
     * @return a valid Java variable name
     */
    private String toValidJavaName(String name) {
        String cleaned = name.replaceAll("[^a-zA-Z0-9]", "_").replaceAll("^(\\d)", "_$1");

        // Convert to camelCase
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        for (char c : cleaned.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                result.append(capitalizeNext ? Character.toUpperCase(c) : Character.toLowerCase(c));
                capitalizeNext = false;
            }
        }
        return result.toString();
    }

    /**
     * Converts a string to a valid Java class name by replacing invalid characters with underscores
     * and converting to PascalCase.
     * @param name the original name
     * @return a valid Java class name
     */
    private String toValidJavaClassName(String name) {
        String cleaned = name.replaceAll("[^a-zA-Z0-9]", "_").replaceAll("^(\\d)", "_$1");

        // Convert to PascalCase
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : cleaned.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                result.append(capitalizeNext ? Character.toUpperCase(c) : Character.toLowerCase(c));
                capitalizeNext = false;
            }
        }
        return result.toString();
    }

    /**
     * Represents a node in the resource tree, which can be a folder containing files and subfolders.
     * @param name the folder name
     * @param path the relative path from resources root
     * @param children map of child folder names to their ResourceNode
     * @param files list of ResourceFile objects in this folder
     */
    private record ResourceNode(String name, String path, Map<String, ResourceNode> children,
                                List<ResourceFile> files) {
        public ResourceNode(String name, String path) {
            this(name, path, new LinkedHashMap<>(), new ArrayList<>());
        }
    }

    /**
     * Represents a resource file with name and path.
     * @param name the file name
     * @param path the relative path from resources root
     */
    private record ResourceFile(String name, String path) {
    }
}