package edu.wpi.first.wpilib.opencv.installer;

import edu.wpi.first.wpilib.opencv.installer.platform.Platform;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class MainCLI {

    /**
     * Main entry point.
     */
    public static void main(String[] args) throws ParseException {
        CommandLineParser p = new DefaultParser();
        Options options = new Options() {{
            addOption(Option.builder("j")
                    .longOpt("java")
                    .optionalArg(true)
                    .numberOfArgs(1)
                    .argName("install-path")
                    .desc("Install the OpenCV Java library")
                    .build()
            );
            addOption(Option.builder("i")
                    .longOpt("jni")
                    .optionalArg(true)
                    .numberOfArgs(1)
                    .argName("install-path")
                    .desc("Install the OpenCV JNI bindings")
                    .build()
            );
            addOption(Option.builder("s")
                    .longOpt("headers")
                    .optionalArg(true)
                    .numberOfArgs(1)
                    .argName("install-path")
                    .desc("Install the OpenCV C++ headers")
                    .build()
            );
            addOption(Option.builder("n")
                    .longOpt("natives")
                    .optionalArg(true)
                    .numberOfArgs(1)
                    .argName("install-path")
                    .desc("Install the OpenCV native libraries")
                    .build()
            );
            addOption("h", "help", false, "Prints this help message");
            addOption("a", "all", false, "Installs all artifacts");
            addOption("v", "version", true, "Set the version of OpenCV to install");
            addOption("o", "overwrite", false, "Overwrite existing files when installing");
            addOption("p", "platform", true, "Install artifacts for a specific platform");
        }};

        // Parse CLI arguments
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
            Installer.setPlatform(Platform.valueOf(parsedArgs.getOptionValue("platform")));
        }
        Platform platform = Installer.getPlatform();
        Installer.setOpenCvVersion(parsedArgs.getOptionValue("version"));
        if (parsedArgs.hasOption("overwrite")) {
            Installer.overwriteExistingFiles();
        }

        // Install selected artifacts
        System.out.println("Installing specified OpenCV components");
        if (parsedArgs.hasOption("java") || parsedArgs.hasOption("all")) {
            try {
                Installer.installJava(parsedArgs.getOptionValue("java", platform.defaultJavaLocation()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (parsedArgs.hasOption("jni") || parsedArgs.hasOption("all")) {
            try {
                Installer.installJni(parsedArgs.getOptionValue("jni", platform.defaultJniLocation()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (parsedArgs.hasOption("headers") || parsedArgs.hasOption("all")) {
            try {
                Installer.installHeaders(parsedArgs.getOptionValue("headers", platform.defaultHeadersLocation()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (parsedArgs.hasOption("natives") || parsedArgs.hasOption("all")) {
            try {
                Installer.installNatives(parsedArgs.getOptionValue("natives", platform.defaultNativesLocation()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("==========================");
        System.out.println("Finished installing OpenCV");
    }

}
