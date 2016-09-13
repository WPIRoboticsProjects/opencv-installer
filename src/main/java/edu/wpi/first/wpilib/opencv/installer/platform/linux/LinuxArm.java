package edu.wpi.first.wpilib.opencv.installer.platform.linux;

/**
 * Platform for ARM Linux (soft float ABI).
 */
public final class LinuxArm extends Linux {

    public static final LinuxArm INSTANCE = new LinuxArm();
    public static final String NAME = "linux-arm";

    private LinuxArm() {

    }

    @Override
    public String name() {
        return NAME;
    }
}
