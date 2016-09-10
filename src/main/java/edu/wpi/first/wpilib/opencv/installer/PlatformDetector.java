package edu.wpi.first.wpilib.opencv.installer;

import lombok.experimental.UtilityClass;

@UtilityClass
class PlatformDetector {

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
    private static String getOs() throws UnsupportedOperatingSystemError {
        if (os != null) {
            return os;
        }
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            os = "win";
        } else if (osName.contains("macos") || osName.contains("mac os")) {
            os = "osx";
        } else if (osName.contains("linux")) {
            os = "linux";
        } else {
            throw new UnsupportedOperatingSystemError(osName);
        }
        return os;
    }

    /**
     * Gets the operating system architecture as one of:
     * <ul>
     * <li>"32"</li>
     * <li>"64"</li>
     * </ul>
     *
     * @return the operating system architecture
     */
    private static String getArch() {
        if (arch != null) {
            return arch;
        }
        String arch = System.getProperty("os.arch");
        if (arch.matches("^(i386|x86)$")) {
            arch = "32";
        } else {
            arch = "64";
        }
        return arch;
    }

    /**
     * Gets the platform this is running on.
     *
     * @return the current platform
     */
    static Platform getPlatform() {
        if (platform != null) {
            return platform;
        }
        getOs();
        getArch();
        switch (os) {
            case "win":
                switch (arch) {
                    case "32":
                        platform = Platform.win32;
                        break;
                    case "64":
                        platform = Platform.win64;
                        break;
                }
            case "osx":
                platform = Platform.osx;
                break;
            case "linux":
                switch (arch) {
                    case "32":
                        platform = Platform.linux32;
                        break;
                    case "64":
                        platform = Platform.linux64;
                        break;
                }
        }
        if (platform == null) {
            throw new UnsupportedOperatingSystemError(os + arch);
        }
        return platform;
    }

}
