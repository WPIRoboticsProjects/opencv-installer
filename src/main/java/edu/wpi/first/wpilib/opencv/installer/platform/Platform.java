package edu.wpi.first.wpilib.opencv.installer.platform;

import edu.wpi.first.wpilib.opencv.installer.platform.linux.Linux32;
import edu.wpi.first.wpilib.opencv.installer.platform.linux.Linux64;
import edu.wpi.first.wpilib.opencv.installer.platform.linux.LinuxArm;
import edu.wpi.first.wpilib.opencv.installer.platform.linux.LinuxArmHf;
import edu.wpi.first.wpilib.opencv.installer.platform.osx.Osx64;
import edu.wpi.first.wpilib.opencv.installer.platform.win.Windows32;
import edu.wpi.first.wpilib.opencv.installer.platform.win.Windows64;

/**
 * Implementations of {@code Platform} define methods for getting the default install locations of the OpenCV libraries.
 */
public interface Platform {

    String USER_HOME = System.getProperty("user.home");

    /**
     * The default install location of the JNI libraries for this platform.
     */
    String defaultJniLocation();

    /**
     * The default install location of the Java libraries for this platform.
     */
    String defaultJavaLocation();

    /**
     * The default install location of the C++ headers for this platform.
     */
    String defaultHeadersLocation();

    /**
     * The default install location of the C++ native libraries for this platform.
     */
    String defaultNativesLocation();

    /**
     * The name of this platform. This should be in the format "${os.name}-${os.arch}", all lowercase, and where
     * {@code os.name} is the name of the operating system (e.g. "win", "linux", "osx") and {@code os.arch} is the
     * operating system architecture (e.g. "x86", "x86_64", "arm").
     *
     * <p>Some examples:
     * <ul>
     * <li>"win-x86_64"</li>
     * <li>"linux-x86"</li>
     * <li>"linux-arm"</li>
     * </ul>
     * </p>
     */
    String name();

    /**
     * @return true if this is a Linux platform
     */
    boolean isLinux();

    /**
     * @return true if this is a Windows platform
     */
    boolean isWindows();

    /**
     * @return true if this is a Mac OS X platform
     */
    boolean isOsx();

    static Platform linux32() {
        return Linux32.INSTANCE;
    }

    static Platform linux64() {
        return Linux64.INSTANCE;
    }

    static Platform linuxArm() {
        return LinuxArm.INSTANCE;
    }

    static Platform linuxArmHf() {
        return LinuxArmHf.INSTANCE;
    }

    static Platform osx64() {
        return Osx64.INSTANCE;
    }

    static Platform windows32() {
        return Windows32.INSTANCE;
    }

    static Platform windows64() {
        return Windows64.INSTANCE;
    }

    /**
     * Gets the platform with the given name.
     *
     * @param name the name of the platform to get
     *
     * @return the platform with the same name as the given one
     *
     * @throws IllegalArgumentException if the platform name is wrong or if the given platform is unsupported
     */
    static Platform valueOf(String name) {
        switch (name) {
            case Linux32.NAME:
                return linux32();
            case Linux64.NAME:
                return linux64();
            case LinuxArm.NAME:
                return linuxArm();
            case LinuxArmHf.NAME:
                return linuxArmHf();
            case Osx64.NAME:
                return osx64();
            case Windows32.NAME:
                return windows32();
            case Windows64.NAME:
                return windows64();
            default:
                throw new IllegalArgumentException("Unknown or unsupported platform: " + name);
        }
    }

}
