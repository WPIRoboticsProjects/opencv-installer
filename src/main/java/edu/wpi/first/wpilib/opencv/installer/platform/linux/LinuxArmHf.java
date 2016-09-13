package edu.wpi.first.wpilib.opencv.installer.platform.linux;

/**
 * Platform for ARM Linux (hard float ABI).
 */
public final class LinuxArmHf extends Linux {

    public static final LinuxArmHf INSTANCE = new LinuxArmHf();
    public static final String NAME = "linux-armhf";

    private LinuxArmHf() {

    }

    @Override
    public String name() {
        return NAME;
    }
}
