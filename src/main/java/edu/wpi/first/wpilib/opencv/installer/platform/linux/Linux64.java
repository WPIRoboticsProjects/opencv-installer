package edu.wpi.first.wpilib.opencv.installer.platform.linux;

/**
 * Platform for 64-bit x86 Linux.
 */
public final class Linux64 extends Linux {

    public static final Linux64 INSTANCE = new Linux64();
    public static final String NAME = "linux-x86_64";

    private Linux64() {

    }

    @Override
    public String name() {
        return NAME;
    }
}
