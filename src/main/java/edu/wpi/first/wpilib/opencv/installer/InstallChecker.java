package edu.wpi.first.wpilib.opencv.installer;

import lombok.experimental.UtilityClass;

import java.util.prefs.Preferences;

/**
 * Utility class for checking if artifacts have already been installed.
 */
@UtilityClass
public class InstallChecker {

    private static final Preferences prefs = Preferences.userNodeForPackage(InstallChecker.class);

    /**
     * Checks if the given artifact type has already been installed by this app.
     *
     * @param which    the artifact type to check
     * @param location the location to check
     * @param version  the version of OpenCV to check for
     *
     * @return true if the given artifact has already been installed, false if it has not
     */
    public static boolean isInstalled(ArtifactType which, String location, String version) {
        return prefs.node(version).getBoolean(location + "_" + which.name(), false);
    }

    /**
     * Sets the given artifact type as having been successfully installed.
     *
     * @param which    the artifact type that was installed
     * @param location the location where OpenCV was installed
     * @param version  the version of OpenCV the artifact is for
     */
    public static void registerSuccessfulInstall(ArtifactType which, String location, String version) {
        prefs.node(version).putBoolean(location + "_" + which.name(), true);
    }

}
