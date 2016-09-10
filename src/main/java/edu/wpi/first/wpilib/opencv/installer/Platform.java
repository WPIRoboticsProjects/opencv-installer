package edu.wpi.first.wpilib.opencv.installer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Enum values for supported platforms.
 */
@Getter
@RequiredArgsConstructor
public enum Platform {

    // TODO figure out Windows install locations
    /**
     * 32-bit Windows.
     */
    win32(
            "",
            "",
            "",
            ""
    ),
    /**
     * 64-bit Windows.
     */
    win64(
            "",
            "",
            "",
            ""
    ),
    /**
     * Mac OS X. Only versions 10.4 and above are supported.
     */
    osx(
            "",
            UserHomeHolder.userHome + "/Library/Java/Extensions",
            "/usr/local/include",
            "/usr/local/lib"
    ),
    /**
     * 32-bit linux.
     */
    linux32(
            "",
            "usr/local",
            "/usr/local/include",
            "/usr/local/lib"
    ),
    /**
     * 64-bit linux.
     */
    linux64(
            "",
            "/usr/local",
            "/usr/local/include",
            "/usr/local/lib"
    );

    private static final class UserHomeHolder {
        static final String userHome = System.getProperty("user.home");
    }

    /**
     * The location of installs for the Java library.
     */
    private final String javaInstallLocation;

    /**
     * The location of installs for the JNI bindings. This should be in a location that is on {@code java.library.path}
     * by default.
     */
    private final String jniInstallLocation;

    /**
     * The location of installs for the C++ header files.
     */
    private final String headersInstallLocation;

    /**
     * The location of installs for the native C++ libraries. This should be a default location on the path
     * (e.g. {@code /usr/local/}).
     */
    private final String nativesInstallLocation;

}
