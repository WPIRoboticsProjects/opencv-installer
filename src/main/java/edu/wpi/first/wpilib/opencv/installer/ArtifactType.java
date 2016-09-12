package edu.wpi.first.wpilib.opencv.installer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtifactType {

    /**
     * The artifact type for the Java library.
     */
    JAVA("Java library"),

    /**
     * The artifact type for the JNI bindings.
     */
    JNI("JNI bindings"),

    /**
     * The artifact type for the C++ headers.
     */
    HEADERS("C++ headers"),

    /**
     * The artifact type for the C++ native libraries.
     */
    NATIVES("C++ native libraries");

    private final String artifactName;
}
