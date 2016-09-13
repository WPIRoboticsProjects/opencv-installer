package edu.wpi.first.wpilib.opencv.installer.platform.win;

/**
 * Platform for 64-bit x86 Windows.
 */
public final class Windows64 extends Windows {

    public static final Windows64 INSTANCE = new Windows64();
    public static final String NAME = "windows-x86_64";

    private Windows64() {

    }

    @Override
    public String name() {
        return NAME;
    }
}
