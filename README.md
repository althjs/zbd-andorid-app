# Android ZBD 프로젝트

이 프로젝트는 Android 애플리케이션입니다.

## 프로덕션 서명 키 설정

이 애플리케이션의 릴리스 버전을 빌드하려면 프로덕션 서명 키가 필요합니다. 서명 키를 설정하는 단계는 다음과 같습니다.

### 1. 키스토어 생성

키스토어가 없다면 `keytool` 명령어를 사용하여 생성합니다. 이 명령어를 실행하면 프로젝트 루트의 `keys/` 디렉토리에 `release.keystore` 파일이 생성됩니다.

```bash
keytool -genkeypair -alias my-app-alias -keyalg RSA -keysize 2048 -validity 10000 -keystore keys/release.keystore
```

이 과정에서 키스토어 비밀번호와 키 비밀번호를 입력하라는 메시지가 표시됩니다. 비밀번호는 6자 이상이어야 합니다. 키 비밀번호는 별도로 묻지 않는 경우 일반적으로 키스토어 비밀번호와 동일합니다.

### 2. `gradle.properties` 설정

프로젝트 루트에 있는 `gradle.properties` 파일에 다음 서명 속성을 추가합니다.

```properties
# 릴리스 빌드를 위한 서명 속성
KEYSTORE_PATH=/Users/althjs/nst/octopus-typing/android-zbd/keys/release.keystore
KEYSTORE_PASSWORD=YOUR_KEYSTORE_PASSWORD # 실제 키스토어 비밀번호로 교체
KEY_ALIAS=my-app-alias
KEY_PASSWORD=YOUR_KEY_PASSWORD # 실제 키 비밀번호로 교체
android.useAndroidX=true
```

**중요:** `YOUR_KEYSTORE_PASSWORD`와 `YOUR_KEY_PASSWORD`를 키스토어 생성 시 설정한 실제 비밀번호로 교체하세요.

### 3. `app/build.gradle.kts` 설정

`app/build.gradle.kts` 파일은 릴리스 빌드에 이 서명 구성을 사용하도록 업데이트되었습니다. 관련 섹션은 다음과 같습니다.

```kotlin
android {
    // ... 기타 설정 ...

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: project.findProperty("KEYSTORE_PATH") as String)
            storePassword = file(System.getenv("KEYSTORE_PASSWORD") ?: project.findProperty("KEYSTORE_PASSWORD") as String)
            keyAlias = file(System.getenv("KEY_ALIAS") ?: project.findProperty("KEY_ALIAS") as String)
            keyPassword = file(System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD") as String)
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
    // ... 기타 설정 ...
}
```

### 4. Gradle Wrapper 및 빌드 환경

개발 과정에서 호환성을 보장하고 빌드 문제를 해결하기 위해 Gradle Wrapper 파일(`gradlew`, `gradle/wrapper/gradle-wrapper.properties`, `gradle/wrapper/gradle-wrapper.jar`)이 다시 생성되었습니다.

`JAVA_HOME` 또는 `ClassNotFoundException` 오류가 발생하는 경우 다음을 확인하세요.
*   **Java 17 설치**: Android Gradle Plugin은 Java 17 이상을 필요로 합니다.
*   **`JAVA_HOME` 올바르게 설정**: 예를 들어, 셸 프로필(`.zshrc` 또는 `.bash_profile`)에 `export JAVA_HOME="/opt/homebrew/opt/openjdk@17"`와 같이 설정합니다.
*   **`JAVA_HOME` 접두사와 함께 `./gradlew build` 실행**: `JAVA_HOME="/opt/homebrew/opt/openjdk@17" ./gradlew build`

## 사용 중단된 API 수정

`app/src/main/java/kr/zbd/android/MainActivity.kt` 파일의 `onBackPressed()` 메서드는 뒤로 가기 이벤트를 처리하기 위해 최신 `OnBackPressedCallback`을 사용하도록 업데이트되어 사용 중단 경고를 해결했습니다.