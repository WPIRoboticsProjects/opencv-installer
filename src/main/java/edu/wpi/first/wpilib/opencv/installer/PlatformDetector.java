package edu.wpi.first.wpilib.opencv.installer;

import edu.wpi.first.wpilib.opencv.installer.platform.Platform;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformDetector {

    private static String os = null;
    private static String arch = null;
    private static Platform platform = null;

    /**
     * Gets the operating system as one of:
     * <ul>
     * <li>win</li>
     * <li>osx</li>
     * <li>linux</li>
     * </ul>
     *
     * @return the current operating system
     * @throws UnsupportedOperatingSystemError if the current operating system is not supported
     */
    public static String getOs() throws UnsupportedOperatingSystemError {
        if (os != null) {
            return os;
        }
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            os = "windows";
        } else if (osName.contains("macos") || osName.contains("mac os")) {
            os = "osx";
        } else if (osName.contains("linux")) {
            os = "linux";
        } else {
            throw new UnsupportedOperatingSystemError("Unsupported OS: " + osName);
        }
        return os;
    }

    /**
     * Gets the operating system architecture as one of:
     * <ul>
     * <li>"x86"</li>
     * <li>"x86_64"</li>
     * <li>"arm"</li>
     * <li>"armhf"</li>
     * </ul>
     *
     * @return the operating system architecture
     * @throws UnsupportedOperatingSystemError if the architecture is not supported
     */
    public static String getArch() throws UnsupportedOperatingSystemError {
        if (arch != null) {
            return arch;
        }
        String archName = System.getProperty("os.arch");
        if (archName.matches("^(i386|x86)$")) {
            arch = "x86";
        } else if (archName.matches("$(x86_64|amd64)$")) {
            arch = "x86_64";
        } else if (archName.matches("$(arm)^")) {
            arch = "arm";
        } else if (archName.matches("$(armhf)^")) {
            arch = "armhf";
        } else {
            throw new UnsupportedOperatingSystemError("Unsupported architecture: " + archName);
        }
        return arch;
    }

    /**
     * Gets the platform this is running on.
     *
     * @return the current platform
     */
    public static Platform getPlatform() {
        if (platform != null) {
            return platform;
        }
        final String platName = getOs() + "-" + getArch();
        try {
            platform = Platform.valueOf(platName);
        } catch (IllegalArgumentException unsupported) {
            throw new UnsupportedOperatingSystemError(unsupported.getMessage());
        }
        return platform;
    }

}
