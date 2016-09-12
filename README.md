# opencv-installer
Simple application to install OpenCV.

This assumes that the OpenCV artifacts are located in the maven repository at `https://github.com/SamCarlberg/opencv-maven/raw/mvn-repo` or in the local maven repository.

(Artifacts for this project are also available in the above repository)

[Link to the maven repo](https://github.com/SamCarlberg/opencv-maven/tree/mvn-repo)

For now, this is just a CLI app with no GUI.

## Supported platforms
This currently supports  

- Windows (32- and 64-bit)  
- Mac OS X 10.4 or higher  
- Linux (32- and 64-bit)  

ARM is not currently supported.

### Windows notes

This doesn't work on Windows. At all. JNI and native binaries need to be built and the install paths need to be figured out.

### Linux notes

JNI and native bindings will be installed in `/usr/local/lib` and the headers will be installed in `/usr/local/include`. If you don't have write access to these folders, you can run the installer with 

`sudo java -Duser.home=$HOME -jar ...`

Make sure that `/usr/local/lib` is on `LD_LIBRARY_PATH` or the JNI bindings won't be loaded by the JVM. 


## Command line arguments

Short name | Long name | Description | Argument
---|---|---|---
| | `help` | Prints the help text | 
`v` | `version` | Sets the OpenCV version | The version in the format `x.x.x` e.g. `3.1.0`
`j` | `java` | Flags the Java API for install. This does _not_ install JNI bindings
`jni` | `jni` | Flags the JNI bindings for install
`h` | `headers` | Flags the C++ headers for install
`n` | `natives` | Flags the C++ native libraries for install
| | `all` | Installs all OpenCV artifacts
| `o` | `overwrite` | Overwrite already installed files
| | `platform` | Download artifacts for a specific platform. They will be located in `./install` | The platform to download artifacts for

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
java -jar opencv-installer --version <version> --platform <platform> --java --jni --headers --natives --overwrite
```

## Using the installer in Gradle build scripts

```groovy
buildscript {
  repositories {
    maven {
      url 'https://github.com/SamCarlberg/opencv-maven/raw/mvn-repo'
    }
  }
  dependencies {
    classpath group: 'edu.wpi.first.wpilib.opencv', name: 'opencv-installer', version: '+'
  }
}

import edu.wpi.first.wpilib.opencv.installer.PlatformDetector

def openCvPlatform = PlatformDetector.getPlatform()
def openCvVersion = "3.1.0"

dependencies {
  compile group: 'org.opencv', name: 'opencv-java', version: openCvVersion
  runtime group: 'org.opencv', name: 'opencv-jni', version: "${openCvPlatform}-${openCvVersion}"
  ...
}
```

## Using the installer as Java library

First, add

```groovy
repositories {
  maven {
    url 'https://github.com/SamCarlberg/opencv-maven/raw/mvn-repo'
  }
}
dependencies {
  compile group: 'edu.wpi.first.wpilib.opencv', name: 'opencv-installer', version: '+'
}
```

to your `build.gradle` file.

Then to make sure that OpenCV is installed prior to using any OpenCV code:

```java
import edu.wpi.first.wpilib.opencv.installer.Installer;
import edu.wpi.first.wpilib.opencv.installer.PlatformDetector;
import org.opencv.core.Core;

class Main {

  static {
    Installer.setOpenCvVersion(Core.VERSION);
    Installer.installJni(PlatformDetector.getPlatform().getDefaultJniInstallLocation());
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

}
```

This will install OpenCV on the current system if the JNI bindings are available for it. If there aren't any JNI bindings, an `IOException` will be thrown by the call to `Installer.installJni()` (you may wrap this in a `try-catch` block if you want to do something with this exception, such as logging it)
