package edu.wpi.first.wpilib.opencv.installer.platform.osx;

/**
 * Platform for 64-bit x86 Mac OS X.
 */
public class Osx64 extends Osx {

    public static final Osx64 INSTANCE = new Osx64();
    public static final String NAME = "osx-x86_64";

    private Osx64() {

    }

    @Override
    public String name() {
        return NAME;
    }
}
