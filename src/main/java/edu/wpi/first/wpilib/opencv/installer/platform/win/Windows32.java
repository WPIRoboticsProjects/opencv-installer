package edu.wpi.first.wpilib.opencv.installer.platform.win;

/**
 * Platform for 32-bit x86 Windows.
 */
public final class Windows32 extends Windows {

    public static final Windows32 INSTANCE = new Windows32();
    public static final String NAME = "windows-x86";

    private Windows32() {

    }

    @Override
    public String name() {
        return NAME;
    }

}
