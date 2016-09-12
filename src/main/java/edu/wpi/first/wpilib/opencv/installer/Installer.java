package edu.wpi.first.wpilib.opencv.installer;

import lombok.experimental.UtilityClass;
import org.apache.commons.cli.*;

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
    private static final String wpilibUrl = "http://first.wpi.edu/FRC/roborio/maven";
    private static final String githubRepo = "https://github.com/SamCarlberg/opencv-maven/raw/mvn-repo";
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
    private static String version = "";


    /**
     * Main entry point.
     */
    public static void main(String[] args) throws ParseException {
        CommandLineParser p = new DefaultParser();
        Options options = new Options() {{
            addOption("j", "java", false, "Install the Java API jar in the working directory");
            addOption("jni", "jni", false, "Install the JNI bindings for this operating system in " + platform.getJniInstallLocation());
            addOption("h", "headers", false, "Install the C++ headers in " + platform.getHeadersInstallLocation());
            addOption("n", "natives", false, "Install the C++ native libraries in " + platform.getNativesInstallLocation());
            addOption("help", "help", false, "Prints this help message");
            addOption("a", "all", false, "Installs all artifacts");
            addOption("v", "version", true, "Set the version of OpenCV to install");
            addOption("o", "overwrite", false, "Overwrite existing files when installing");
            addOption(null, "platform", true, "Install artifacts for a specific platform");
        }};
        CommandLine parsedArgs = p.parse(options, args);
        if (parsedArgs.hasOption("help")) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("opencv-installer", options);
            return;
        }
        if (!parsedArgs.hasOption("version")) {
            throw new MissingOptionException("-v <version>");
        }
        if (parsedArgs.hasOption("platform")) {
            setPlatform(Platform.valueOf(parsedArgs.getOptionValue("platform")));
        }
        setOpenCvVersion(parsedArgs.getOptionValue("version"));
        overwrite = parsedArgs.hasOption("overwrite");
        System.out.println("Installing specified OpenCV components");
        if (parsedArgs.hasOption("java") || parsedArgs.hasOption("all")) {
            try {
                installJava();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (parsedArgs.hasOption("jni") || parsedArgs.hasOption("all")) {
            try {
                installJni();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (parsedArgs.hasOption("headers") || parsedArgs.hasOption("all")) {
            try {
                installHeaders();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (parsedArgs.hasOption("natives") || parsedArgs.hasOption("all")) {
            try {
                installNatives();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("==========================");
        System.out.println("Finished installing OpenCV");
    }

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
        calculateVersion();
        overridePlatform = true;
    }

    /**
     * Sets the version of OpenCV to get artifacts for.
     *
     * @param v the version of OpenCV to install
     */
    public static void setOpenCvVersion(String v) {
        openCvVersion = v;
        calculateVersion();
    }

    private static void calculateVersion() {
        version = platform + "-" + openCvVersion;
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
     * Downloads the Java API jar.
     */
    public static void installJava() throws IOException {
        System.out.println("====================");
        System.out.println("Installing Java");
        System.out.println("====================");
        install(ArtifactType.JAVA);
    }

    /**
     * Installs the JNI bindings.
     */
    public static void installJni() throws IOException {
        System.out.println("====================");
        System.out.println("Installing JNI");
        System.out.println("====================");
        install(ArtifactType.JNI);
    }

    /**
     * Installs the C++ headers.
     */
    public static void installHeaders() throws IOException {
        System.out.println("====================");
        System.out.println("Installing headers");
        System.out.println("====================");
        install(ArtifactType.HEADERS);
    }

    /**
     * Installs the C++ native libraries.
     */
    public static void installNatives() throws IOException {
        System.out.println("====================");
        System.out.println("Installing natives");
        System.out.println("====================");
        install(ArtifactType.NATIVES);
    }

    private static void install(ArtifactType type) throws IOException {
        if (!overridePlatform && InstallChecker.isInstalled(type, openCvVersion)) {
            System.out.println("Artifacts for the version " + openCvVersion + " " + type.getArtifactName() + " have already been installed!");
            if (!overwrite) {
                return;
            }
        }
        String artifactId;
        String v = version;
        String installLocation = "";
        if (overridePlatform) {
            installLocation = "install";
        }
        switch (type) {
            case JAVA:
                artifactId = javaJarName;
                v = openCvVersion;
                installLocation += platform.getJavaInstallLocation();
                break;
            case JNI:
                artifactId = jniName;
                installLocation += platform.getJniInstallLocation();
                break;
            case HEADERS:
                artifactId = headersName;
                v = openCvVersion;
                installLocation += platform.getHeadersInstallLocation();
                break;
            case NATIVES:
                artifactId = nativesName;
                installLocation += platform.getNativesInstallLocation();
                break;
            default:
                throw new UnsupportedOperationException("Unknown artifact type: " + type);
        }
        URL remote = resolveRemote(artifactId, v);
        File local = resolveLocal(artifactId, v);
        File source;
        if (!local.exists()) {
            copyToMavenLocal(githubRepo, groupId, artifactId, v);
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
            InstallChecker.registerSuccessfulInstall(type, openCvVersion);
        }
    }

    private static URL resolveRemote(String artifactId, String version) throws MalformedURLException {
        return new URL(resolveRelative(githubRepo, artifactId, version));
    }

    private static File resolveLocal(String artifactId, String version) {
        return new File(resolveRelative(mavenLocal, artifactId, version));
    }

    private static String resolveRelative(String repo, String artifactId, String version) {
        return String.format("%s/%s/%s/%s/%s-%s.jar", repo, groupId.replace('.', '/'), artifactId, version, artifactId, version);
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
                if (Files.isDirectory(dst)) {
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
    private static Path copyToMavenLocal(String repo, String groupId, String artifactId, String version) throws IOException {
        String remoteDir = String.format("%s/%s/%s/%s/", repo, groupId.replace('.', '/'), artifactId, version);
        String name = String.format("%s-%s", artifactId, version);
        String dstDir = remoteDir.replace(repo, mavenLocal);
        if (!Files.exists(Paths.get(dstDir))) {
            Files.createDirectories(Paths.get(dstDir));
        }

        String jar = name + ".jar";
        String jarPath = remoteDir + jar;
        System.out.println("Copying " + jarPath + " to the local maven repository");
        Files.deleteIfExists(Paths.get(dstDir, jar));
        Files.copy(new URL(jarPath).openStream(), Paths.get(dstDir, jar));

        String pom = name + ".pom";
        String pomPath = remoteDir + pom;
        Files.deleteIfExists(Paths.get(dstDir, pom));
        Files.copy(new URL(pomPath).openStream(), Paths.get(dstDir, pom));

        return Paths.get(dstDir, jar);
    }

}
