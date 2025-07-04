# Android ZBD Project

This project is an Android application.

## Production Signing Key Configuration

To build a release version of this application, a production signing key is required. Follow these steps to set up the signing key:

### 1. Generate a Keystore

If you don't have a keystore, generate one using the `keytool` command. This will create a `release.keystore` file in the `keys/` directory at the project root.

```bash
keytool -genkeypair -alias my-app-alias -keyalg RSA -keysize 2048 -validity 10000 -keystore keys/release.keystore
```

During this process, you will be prompted to enter a keystore password and a key password. Ensure these passwords are at least 6 characters long. The key password will typically be the same as the keystore password if not prompted separately.

### 2. Configure `gradle.properties`

Add the following signing properties to your `gradle.properties` file (located at the project root):

```properties
# Signing properties for release build
KEYSTORE_PATH=/Users/althjs/nst/octopus-typing/android-zbd/keys/release.keystore
KEYSTORE_PASSWORD=YOUR_KEYSTORE_PASSWORD # Replace with your actual keystore password
KEY_ALIAS=my-app-alias
KEY_PASSWORD=YOUR_KEY_PASSWORD # Replace with your actual key password
android.useAndroidX=true
```

**Important:** Replace `YOUR_KEYSTORE_PASSWORD` and `YOUR_KEY_PASSWORD` with the actual passwords you set during keystore generation.

### 3. Configure `app/build.gradle.kts`

The `app/build.gradle.kts` file has been updated to use these signing configurations for release builds. The relevant section looks like this:

```kotlin
android {
    // ... other configurations ...

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: project.findProperty("KEYSTORE_PATH") as String)
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: project.findProperty("KEYSTORE_PASSWORD") as String)
            keyAlias = System.getenv("KEY_ALIAS") ?: project.findProperty("KEY_ALIAS") as String)
            keyPassword = System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD") as String)
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    // ... other configurations ...
}
```

### 4. Gradle Wrapper and Build Environment

During development, the Gradle Wrapper files (`gradlew`, `gradle/wrapper/gradle-wrapper.properties`, `gradle/wrapper/gradle-wrapper.jar`) were re-generated to ensure compatibility and resolve build issues.

If you encounter `JAVA_HOME` or `ClassNotFoundException` errors, ensure:
*   **Java 17 is installed**: Android Gradle Plugin requires Java 17 or higher.
*   **`JAVA_HOME` is set correctly**: For example, `export JAVA_HOME="/opt/homebrew/opt/openjdk@17"` in your shell profile (`.zshrc` or `.bash_profile`).
*   **Run `./gradlew build` with `JAVA_HOME` prefix**: `JAVA_HOME="/opt/homebrew/opt/openjdk@17" ./gradlew build`

## Deprecated API Fixes

The `onBackPressed()` method in `app/src/main/java/kr/zbd/android/MainActivity.kt` has been updated to use the modern `OnBackPressedCallback` for handling back press events, resolving a deprecation warning.
