package edu.wpi.first.wpilib.opencv.installer.platform.linux;

/**
 * Platform for 32-bit x86 Linux.
 */
public final class Linux32 extends Linux {

    public static final Linux32 INSTANCE = new Linux32();
    public static final String NAME = "linux-x86";

    private Linux32() {

    }

    @Override
    public String name() {
        return NAME;
    }
}
