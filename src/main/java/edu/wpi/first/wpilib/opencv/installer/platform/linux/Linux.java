package edu.wpi.first.wpilib.opencv.installer.platform.linux;

import edu.wpi.first.wpilib.opencv.installer.platform.Platform;

/**
 * Abstract superclass for linux platforms.
 */
abstract class Linux implements Platform {

    @Override
    public String defaultJniLocation() {
        return "/usr/local/lib";
    }

    @Override
    public String defaultJavaLocation() {
        return "";
    }

    @Override
    public String defaultHeadersLocation() {
        return "/usr/local/include";
    }

    @Override
    public String defaultNativesLocation() {
        return "/usr/local/lib";
    }

    @Override
    public final boolean isLinux() {
        return true;
    }

    @Override
    public final boolean isWindows() {
        return false;
    }

    @Override
    public final boolean isOsx() {
        return false;
    }

}
