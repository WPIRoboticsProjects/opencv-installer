package edu.wpi.first.wpilib.opencv.installer;

import lombok.experimental.UtilityClass;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
 */
@UtilityClass
public class Installer {

    private static final String userHome = System.getProperty("user.home");
    private static final String wpilibUrl = "http://first.wpi.edu/FRC/roborio/maven";
    private static final String localMaven = userHome + "/.m2/repository";
    private static final Path tmpDir;
    private static final Path unzippedDir;
    private static boolean overwrite = true;

    static {
        try {
            tmpDir = Files.createTempDirectory("opencv-installer");
            unzippedDir = Files.createTempDirectory(tmpDir, "unzipped");
        } catch (IOException e) {
            throw new InternalError("Could not create temp directory", e);
        }
    }

    private static final Platform platform = PlatformDetector.getPlatform();
    private static final String groupId = "org.opencv";
    private static final String javaJarName = "opencv-java";
    private static final String jniName = "opencv-jni";
    private static final String headersName = "opencv-headers";
    private static final String nativesName = "opencv-natives";
    private static String openCvVersion = "";
    private static String version = "";

    private enum ArtifactType {
        JAVA, // This one has special cases because the jar isn't packaged, so unzipping it would just output a ton of class files
        JNI,
        HEADERS,
        NATIVES
    }


    public static void main(String... args) throws ParseException {
        CommandLineParser p = new DefaultParser();
        Options options = new Options() {{
            addOption("j", "java", false, "Install the Java API jar in the working directory");
            addOption("jni", "jni", false, "Install the JNI bindings for this operating system in " + platform.getJniInstallLocation());
            addOption("h", "headers", false, "Install the C++ headers in " + platform.getHeadersInstallLocation());
            addOption("n", "natives", false, "Install the C++ native libraries in " + platform.getNativesInstallLocation());
            addOption("help", "help", false, "Prints this help message");
            addOption("a", "all", false, "Installs all artifacts");
            addOption("v", "version", true, "Set the version of OpenCV to install");
            addOption(null, "no-overwrite", false, "Don't overwrite existing files when installing");
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
        openCvVersion = parsedArgs.getOptionValue("version");
        version = platform + "-" + openCvVersion;
        overwrite = !parsedArgs.hasOption("no-overwrite");
        System.out.println("Installing specified OpenCV components");
        System.out.println("======================================");
        if (parsedArgs.hasOption("java") || parsedArgs.hasOption("all")) {
            installJava();
        }
        if (parsedArgs.hasOption("jni") || parsedArgs.hasOption("all")) {
            installJni();
        }
        if (parsedArgs.hasOption("headers") || parsedArgs.hasOption("all")) {
            installHeaders();
        }
        if (parsedArgs.hasOption("natives") || parsedArgs.hasOption("all")) {
            installNatives();
        }

        System.out.println("==========================");
        System.out.println("Finished installing OpenCV");
    }

    private static void installJava() {
        System.out.println("Installing Java");
        install(ArtifactType.JAVA);
    }

    private static void installJni() {
        System.out.println("Installing JNI");
        install(ArtifactType.JNI);
    }

    private static void installHeaders() {
        System.out.println("Installing headers");
        install(ArtifactType.HEADERS);
    }

    private static void installNatives() {
        System.out.println("Installing natives");
        install(ArtifactType.NATIVES);
    }

    private static void install(ArtifactType type) {
        try {
            String artifactId;
            String v = version;
            String installLocation;
            switch (type) {
                case JAVA:
                    artifactId = javaJarName;
                    v = openCvVersion;
                    installLocation = platform.getJavaInstallLocation();
                    break;
                case JNI:
                    artifactId = jniName;
                    installLocation = platform.getJniInstallLocation();
                    break;
                case HEADERS:
                    artifactId = headersName;
                    installLocation = platform.getHeadersInstallLocation();
                    break;
                case NATIVES:
                    artifactId = nativesName;
                    installLocation = platform.getNativesInstallLocation();
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown artifact type: " + type);
            }
            URL remote = resolveRemote(artifactId, v);
            File local = resolveLocal(artifactId, v);
            File source;
            if (local.exists()) {
                System.out.println("Using local file at " + local.toURI());
                source = local;
            } else if (urlExists(remote)) {
                System.out.println("Using remote file at " + remote);
                source = download(remote, artifactId, v);
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
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private static URL resolveRemote(String artifactId, String version) throws MalformedURLException {
        return new URL(resolveRelative(wpilibUrl, artifactId, version));
    }

    private static File resolveLocal(String artifactId, String version) {
        return new File(resolveRelative(localMaven, artifactId, version));
    }

    private static String resolveRelative(String repo, String artifactId, String version) {
        return repo + '/' + groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/' + artifactId + '-' + version + ".jar";
    }

    /**
     * Checks if the given URL exists
     *
     * @param url the url to check
     * @return true if the url exists, false if it does not
     * @throws IOException if the URL is malformed
     */
    private static boolean urlExists(URL url) throws IOException {
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setConnectTimeout(1000);
        c.setRequestMethod("GET");
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
        c.connect();
        return c.getResponseCode() == HttpURLConnection.HTTP_OK;
    }

    /**
     * Downloads the artifact at the given remote maven repository
     *
     * @param url        the root repository url
     * @param artifactId the id of the artifact to download
     * @param version    the version of the artifact to download
     * @return the location of the downloaded artifact
     * @throws IOException if the artifact could not be downloaded or a file could not be created for it
     */
    private static File download(URL url, String artifactId, String version) throws IOException {
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setConnectTimeout(1000);
        c.setRequestMethod("GET");
        c.connect();
        InputStream is = c.getInputStream();
        Path downloadLocation = Files.createTempFile(tmpDir, artifactId + '-' + version, ".jar");
        Files.copy(is, Files.createTempFile(tmpDir, artifactId + '-' + version, ".jar"));
        return downloadLocation.toAbsolutePath().toFile();
    }

    /**
     * Unzips the given zip file
     *
     * @param zipFile the file to unzip
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
                if (Files.exists(dst)) {
                    System.out.println("    Destination file already exists, overwriting");
                    Files.delete(dst);
                }
                Files.copy(src, dst);
            }
        } catch (IOException e) {
            throw new RuntimeException((e));
        }
    }

}
