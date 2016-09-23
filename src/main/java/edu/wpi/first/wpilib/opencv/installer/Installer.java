package edu.wpi.first.wpilib.opencv.installer;

import edu.wpi.first.wpilib.opencv.installer.platform.Platform;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A command line application that downloads and installs OpenCV. This assumes that the OpenCV artifacts will be
 * available on the FRC maven server at http://first.wpi.edu/FRC/roborio/maven or in the local maven repository.
 *
 * <p>Install locations are specified by the current {@link Platform}</p>
 */
@UtilityClass
public class Installer {

    private static final String userHome = System.getProperty("user.home");
    private static final String mavenUrl = "http://first.wpi.edu/FRC/roborio/maven/development";
    private static final String mavenLocal = userHome + "/.m2/repository";
    private static final Path tmpDir;
    private static final Path unzippedDir;
    private static boolean overwrite = false;

    static {
        try {
            tmpDir = Files.createTempDirectory("opencv-installer");
            unzippedDir = Files.createTempDirectory(tmpDir, "unzipped");
        } catch (IOException e) {
            throw new InternalError("Could not create temp directory", e);
        }
    }

    private static Platform platform = PlatformDetector.getPlatform();
    private static boolean overridePlatform = false;
    private static final String groupId = "org.opencv";
    private static final String javaJarName = "opencv-java";
    private static final String jniName = "opencv-jni";
    private static final String headersName = "opencv-headers";
    private static final String nativesName = "opencv-natives";
    private static String openCvVersion = "";

    /**
     * Sets a specific platform to install. Artifacts will be downloaded into the working directory and will need to be
     * manually installed.
     *
     * <p><strong>The platform is auto-detected when this class is loaded</strong>; this method should only be used
     * when downloading artifacts for a different operating system or architecture.</p>
     *
     * @param p the platform to get the artifacts for
     */
    public static void setPlatform(Platform p) {
        platform = p;
        overridePlatform = true;
    }

    /**
     * Gets the platform that artifacts will be installed for. This will return the platform that the installer is
     * running on, unless it's been overridden by {@link #setPlatform(Platform)}.
     *
     * @return the current platform
     */
    public static Platform getPlatform() {
        return platform;
    }

    /**
     * Sets the version of OpenCV to get artifacts for.
     *
     * @param v the version of OpenCV to install
     */
    public static void setOpenCvVersion(String v) {
        openCvVersion = v;
    }

    /**
     * Gets the version of OpenCV being installed.
     *
     * @return the version of OpenCV being installed
     */
    public static String getOpenCvVersion() {
        return openCvVersion;
    }

    /**
     * Overwrites existing files when installing.
     */
    public static void overwriteExistingFiles() {
        overwrite = true;
    }

    /**
     * Downloads the Java API jar.
     */
    public static void installJava(String location) throws IOException {
        System.out.println("====================");
        System.out.println("Installing Java to " + location);
        System.out.println("====================");
        install(ArtifactType.JAVA, location);
    }

    /**
     * Installs the JNI bindings.
     */
    public static void installJni(String location) throws IOException {
        System.out.println("====================");
        System.out.println("Installing JNI to " + location);
        System.out.println("====================");
        install(ArtifactType.JNI, location);
    }

    /**
     * Installs the C++ headers.
     */
    public static void installHeaders(String location) throws IOException {
        System.out.println("====================");
        System.out.println("Installing headers to " + location);
        System.out.println("====================");
        install(ArtifactType.HEADERS, location);
    }

    /**
     * Installs the C++ native libraries.
     */
    public static void installNatives(String location) throws IOException {
        System.out.println("====================");
        System.out.println("Installing natives to " + location);
        System.out.println("====================");
        install(ArtifactType.NATIVES, location);
    }

    private static void install(ArtifactType type, String location) throws IOException {
        if (!Paths.get(location).isAbsolute()) {
            // Force location to be an absolute path
            location = Paths.get(location).toAbsolutePath().toString();
        }
        if (!overridePlatform && InstallChecker.isInstalled(type, location, openCvVersion)) {
            System.out.println("Artifacts for the version " + openCvVersion + " " + type.getArtifactName() + " have already been installed!");
            if (!overwrite) {
                return;
            }
        }
        String artifactId;
        String v = openCvVersion;
        String classifier = null;
        String installLocation = location;
        if (overridePlatform) {
            installLocation = "install/" + location;
        }
        switch (type) {
            case JAVA:
                artifactId = javaJarName;
                break;
            case JNI:
                artifactId = jniName;
                classifier = platform.name();
                break;
            case HEADERS:
                artifactId = headersName;
                break;
            case NATIVES:
                artifactId = nativesName;
                classifier = platform.name();
                break;
            default:
                throw new UnsupportedOperationException("Unknown artifact type: " + type);
        }
        URL remote = resolveRemote(artifactId, v, classifier);
        File local = resolveLocal(artifactId, v, classifier);
        File source;
        if (!local.exists()) {
            copyToMavenLocal(mavenUrl, artifactId, v, classifier);
        }
        if (local.exists()) {
            System.out.println("Using local file at " + local.toURI());
            source = local;
        } else {
            throw new NoSuchFileException("Could not find artifacts. Looked in:\n" +
                    "        " + remote + "\n" +
                    "        " + local.toURI());
        }
        Path unzipped;
        if (type != ArtifactType.JAVA) {
            unzipped = unzip(source);
        } else {
            Path dst = unzippedDir.resolve(artifactId + '-' + v).resolve(artifactId + '-' + v + ".jar");
            Files.createDirectories(dst.getParent());
            Files.copy(source.toPath(), dst);
            System.out.println("  Downloaded Java to " + dst);
            unzipped = dst.getParent();
        }
        copyAll(unzipped, Paths.get(installLocation));
        if (!overridePlatform) {
            InstallChecker.registerSuccessfulInstall(type, location, openCvVersion);
        }
    }

    private static URL resolveRemote(String artifactId, String version, String classifier) throws MalformedURLException {
        return new URL(resolveRelative(mavenUrl, artifactId, version, classifier));
    }

    private static File resolveLocal(String artifactId, String version, String classifier) {
        File local = new File(resolveRelative(mavenLocal, artifactId, version, classifier));
        System.out.println("Local = " + local.getAbsolutePath());
        return local;
    }

    private static String resolveRelative(String repo, String artifactId, String version, String classifier) {
        return String.format(
                "%s/%s.jar",
                resolveDir(repo, groupId, artifactId, version),
                resolveFullArtifactName(artifactId, version, classifier)
        );
    }

    /**
     * Unzips the given zip file
     *
     * @param zipFile the file to unzip
     *
     * @return the directory that the file was unzipped into
     */
    private static Path unzip(File zipFile) {
        final Path dstDir = unzippedDir.resolve(zipFile.getName().replaceAll("^(.*)\\.(.*)$", "$1") + "/");
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            Files.createDirectories(dstDir);
            for (ZipEntry e = zis.getNextEntry(); e != null; e = zis.getNextEntry()) {
                String fileName = e.getName();
                if (fileName.contains("META-INF")) {
                    // This stuff doesn't matter, don't bother extracting it
                    continue;
                }
                System.out.println("  File: " + fileName);
                Path dst = dstDir.resolve(fileName);
                System.out.println("    Unzipping to " + dst);
                if (!Files.exists(dst.getParent())) {
                    Files.createDirectories(dst.getParent());
                }
                if (!e.isDirectory()) {
                    Files.deleteIfExists(dst);
                    Files.copy(zis, dst);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not unzip " + zipFile, e);
        }
        return dstDir;
    }

    /**
     * Copies all files from the source directory to the destination directory.
     *
     * @param sourceDir the directory holding the files to copy
     * @param dstDir    the directory to copy the files into
     *
     * @throws IOException if the source directory is unreadable
     */
    private static void copyAll(Path sourceDir, Path dstDir) throws IOException {
        System.out.println("Copying all files from " + sourceDir + " into " + dstDir);
        Files.list(sourceDir)
                .forEach(p -> unsafeCopy(p, dstDir.resolve(p.getFileName())));
    }

    private static void unsafeCopy(Path src, Path dst) {
        try {
            if (overwrite) {
                System.out.println("  Copying " + src.toAbsolutePath() + " to " + dst.toAbsolutePath());
                if (dst.getParent() != null && !Files.exists(dst.getParent())) {
                    Files.createDirectories(dst.getParent());
                }
                if (Files.isDirectory(src)) {
                    copyAll(src, dst);
                } else {
                    if (Files.exists(dst)) {
                        System.out.println("    Destination file already exists, overwriting");
                        Files.delete(dst);
                    }
                    Files.copy(src, dst);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException((e));
        }
    }

    /**
     * Copies a remote library to the local maven repository. This only downloads the .jar and the .pom
     *
     * @return the path to the copied jar
     */
    private static Path copyToMavenLocal(String repo, String artifactId, String version, String classifier) throws IOException {
        String remoteDir = resolveDir(repo, groupId, artifactId, version);
        String dstDir = remoteDir.replace(repo, mavenLocal);
        if (!Files.exists(Paths.get(dstDir))) {
            Files.createDirectories(Paths.get(dstDir));
        }

        String jar = resolveFullArtifactName(artifactId, version, classifier) + ".jar";
        String jarPath = remoteDir + jar;
        System.out.println("Copying " + jarPath + " to the local maven repository");
        Files.deleteIfExists(Paths.get(dstDir, jar));
        Files.copy(new URL(jarPath).openStream(), Paths.get(dstDir, jar));

        String pom =  String.format("%s-%s.pom", artifactId, version);
        String pomPath = remoteDir + pom;
        Files.deleteIfExists(Paths.get(dstDir, pom));
        Files.copy(new URL(pomPath).openStream(), Paths.get(dstDir, pom));

        return Paths.get(dstDir, jar);
    }

    public static String resolveDir(String repo, String group, String artifact, String version) {
        return String.format(
                "%s/%s/%s/%s",
                repo,
                group.replace('.', '/'),
                artifact,
                version
        );
    }

    public static String resolveFullArtifactName(String artifact, String version, String classifier) {
        return String.format(
                "%s-%s%s",
                artifact,
                version,
                classifier == null ? "" : "-" + classifier
        );
    }

}
