package edu.wpi.first.wpilib.opencv.installer.platform.win;

import edu.wpi.first.wpilib.opencv.installer.platform.Platform;

/**
 * Abstract superclass for windows platforms. Defaults to installing artifacts in {@code C:\Users\<user>\OpenCV}.
 */
public abstract class Windows implements Platform {

    private static final String OPENCV_DIR = USER_HOME + "\\OpenCV";

    @Override
    public String defaultJniLocation() {
        return OPENCV_DIR + "\\jni";
    }

    @Override
    public String defaultJavaLocation() {
        return OPENCV_DIR + "\\java";
    }

    @Override
    public String defaultHeadersLocation() {
        return OPENCV_DIR + "\\include";
    }

    @Override
    public String defaultNativesLocation() {
        return OPENCV_DIR + "\\lib";
    }

    @Override
    public final boolean isWindows() {
        return true;
    }

    @Override
    public final boolean isLinux() {
        return false;
    }

    @Override
    public final boolean isOsx() {
        return false;
    }

}
