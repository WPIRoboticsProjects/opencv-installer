package edu.wpi.first.wpilib.opencv.installer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
enum Platform {

    // TODO figure out Windows install locations
    win32(
            "",
            "",
            "",
            ""
    ),
    win64(
            "",
            "",
            "",
            ""
    ),
    osx(
            "",
            UserHomeHolder.userHome + "/Library/Java/Extensions",
            "/usr/local/include",
            "/usr/local/lib"
    ),
    linux32(
            "",
            "usr/local",
            "/usr/local/include",
            "/usr/local/lib"
    ),
    linux64(
            "",
            "/usr/local",
            "/usr/local/include",
            "/usr/local/lib"
    );

    private static final class UserHomeHolder {
        static final String userHome = System.getProperty("user.home");
    }

    private final String javaInstallLocation;
    private final String jniInstallLocation;
    private final String headersInstallLocation;
    private final String nativesInstallLocation;

}
