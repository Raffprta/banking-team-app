## CSC2022 Team 19 Git Repository

### Introduction
This repository is to be used for all programming-related work in connection with the **CSC2022 Team Project**.

### Structure 
* **/experimental** - Mockups and experiments go here.
* **/keys** - Security keys required for signing the experimental code and using the shared API keys included in the source.
* ... (more to follow)

### Preparing the build environment
The recommended tools for building are currently:

* **Android Studio 0.8.14** - [available here](http://tools.android.com/download/studio/canary/latest) for Windows/OS X/Linux.
  * *N.B.* Android Studio is constantly evolving with new features and fixes, and the pre-packaged beta bundles are a bit behind. A manual install of the latest Canary (experimental/bleeding-edge) build is recommended. The latest builds are able to self-update going forward.
* **Android SDK Tools / SDK 21 (Lollipop)** - [available here](https://developer.android.com/sdk/index.html). Windows users, grab the installer.

For emulating Android devices:

* ***EASY*** - **Official Android device emulator** (included in SDK) with [Intel HAXM Android device emulator acceleration package](https://software.intel.com/en-us/android/articles/intel-hardware-accelerated-execution-manager). This package will speed up official Google Android emulators using **x86** or **x86_64** CPU architectures. *Highly recommended*.
  * **NOTE**: When creating Android virtual devices with the official emulator, be sure to choose **x86_64** or **x86** CPU architectures and not **ARM**. Also, be   sure to choose a target including **Google APIs (Lollipop)**.

***OR...***

* ***DIFFICULT*** - **Genymotion** Android device emulator. However, by default it does not include **Google Play Services**, which is required to test Google APIs, e.g. Google Maps, Google Play Games, Google Places etc. This can be added **unofficially** by [referring to this guide](https://gist.github.com/wbroek/9321145), but it is not an easy process.

To install the build environment under **Windows**:

1. Download and install [JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) if you don't have it already.
2. Add the **JAVA_HOME** environment variable to your system:
   * Go to **Control Panel > System > Advanced tab > Environment Variables**.
   * Under **System variables**, click **New**.
   * Enter **JAVA_HOME** as the variable name.
   * Enter the path to your JDK as the value, e.g. `C:\Program Files\Java\jdk1.7.0_71`. *Do not include \bin\ on the end.*
   * Hit **OK** and close the System Control Panel.
3. Unzip Android Studio to a convenient location (e.g. `C:\Program Files\Android Studio`).
4. Create a shortcut to `studio64.exe`, found in the `Android Studio\bin` folder. Name the shortcut *Android Studio*.
5. Install the SDK tools.
6. Run the **Android SDK Manager**, and use it to download and install **Android SDK v21 (Lollipop)**.
7. Download and install the [Intel HAXM package](https://software.intel.com/en-us/android/articles/intel-hardware-accelerated-execution-manager). This component should dramatically speed up the official Android emulator, which is easier to use than Genymotion when Google API support is required.
8. Launch Android Studio. If you used the SDK installer, it should find the SDK location automatically and allow you to create a new project or import an existing one.
9. Copy the `debug.keystore` file from this repository to `C:\Users\<yourname>\.android\` (overwriting any existing file). This ensures Android Studio will sign your compiled applications with the same security code as other team members, allowing us to share Google API keys.

If you want to build a project from this repository, use the *Import Project...* feature of Android Studio to import it and generate the necessary files specific to your system.