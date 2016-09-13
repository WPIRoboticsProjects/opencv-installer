package edu.wpi.first.wpilib.opencv.installer.platform.osx;

import edu.wpi.first.wpilib.opencv.installer.platform.Platform;

/**
 * Abstract superclass for Mac OS X platforms.
 */
public abstract class Osx implements Platform {

    @Override
    public String defaultJniLocation() {
        return USER_HOME + "/Library/Java/Extensions";
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
    public final boolean isOsx() {
        return true;
    }

    @Override
    public final boolean isLinux() {
        return false;
    }

    @Override
    public final boolean isWindows() {
        return false;
    }
}
