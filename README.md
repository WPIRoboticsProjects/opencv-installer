# OpenCvInstaller
Simple application to install OpenCV.

This assumes that the OpenCV artifacts are located in the FRC maven repository at `http://first.wpi.edu/FRC/roborio/maven/` or in the local maven repository.

For now, this is just a CLI app with no GUI.

## Supported platforms
This currently supports  

- Windows (32- and 64-bit)  
- Mac OS X 10.4 or higher  
- Linux (32- and 64-bit)  

ARM is not currently supported.

## Command line arguments

Short name | Long name | Description | Argument
---|---|---|---
| | `--help` | Prints the help text | 
`-v` | `--version` | Sets the OpenCV version | The version in the format `x.x.x` e.g. `3.1.0`
`-j` | `--java` | Flags the Java API for install. This does _not_ install JNI bindings
`-jni` | `--jni` | Flags the JNI bindings for install
`-h` | `--headers` | Flags the C++ headers for install
`-n` | `--natives` | Flags the C++ native libraries for install
| | `--all` | Installs all OpenCV artifacts
| `-o` | `--overwrite` | Overwrite already installed files
| | `--platform` | Download artifacts for a specific platform. They will be located in `./install` | The platform to download artifacts for

#### Options for `platform`
```
win32
win64
osx
linux32
linux64
```

## Usage
```
java -jar opencv-installer --version <version> --platform <platform> --java --jni --headers --natives --no-overwrite
```
