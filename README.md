# Android ZBD 프로젝트

이 프로젝트는 Android 애플리케이션입니다.

## 📋 버전 정보

### v1.0.0 (2025-07-04)
- 첫 번째 공식 릴리즈
- 기본 웹뷰 기능 및 앱 링크 지원
- 파일 업로드 및 사진 촬영 기능 추가
- Google OAuth 연동 지원

## 🔧 주요 기능

- **웹뷰 기반 앱**: zbd.kr 웹사이트를 네이티브 앱으로 제공
- **앱 링크 지원**: 웹에서 앱으로 원활한 연결
- **파일 업로드**: 갤러리에서 파일 선택 가능
- **사진 촬영**: 카메라로 직접 사진 촬영 가능
- **권한 관리**: 필요한 권한 자동 요청 및 관리

## 🚀 최근 업데이트 (v1.0.0)

### 추가된 기능
- 📷 **카메라 권한 추가**: 웹페이지에서 사진 촬영 가능
- 📁 **파일 액세스 권한**: 갤러리 및 파일 시스템 접근 가능
- 🔧 **WebChromeClient**: 파일 업로드 및 다운로드 지원
- 🛡️ **FileProvider**: 안전한 파일 공유 메커니즘
- 📱 **동적 권한 요청**: 런타임 권한 자동 처리

### 기술적 개선사항
- Android 13+ 미디어 권한 지원
- 하위 버전 호환성 보장
- 사용자 경험 개선된 파일 선택 인터페이스

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

## 프로덕션 빌드 및 설치

### 프로덕션 모드 빌드

릴리즈 버전의 APK를 빌드하려면 다음 명령어를 실행합니다:

```bash
# Java 17 환경에서 릴리즈 빌드
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.15/libexec/openjdk.jdk/Contents/Home
./gradlew assembleRelease
```

빌드가 완료되면 APK 파일이 다음 위치에 생성됩니다:
- `app/build/outputs/apk/release/app-release.apk`

### 휴대폰에 설치

#### 방법 1: ADB를 통한 직접 설치 (USB 연결)

1. **개발자 옵션 활성화**
   - 설정 > 휴대폰 정보 > 빌드 번호를 7회 터치
   - 설정 > 개발자 옵션 > USB 디버깅 활성화

2. **USB 케이블로 휴대폰과 컴퓨터 연결**

3. **연결된 기기 확인**
   ```bash
   adb devices
   ```

4. **APK 설치**
   ```bash
   # 기존 앱이 있다면 제거
   adb uninstall kr.zbd.android
   
   # 새 APK 설치
   adb install app/build/outputs/apk/release/app-release.apk
   ```

#### 방법 2: 파일 전송 후 수동 설치

1. **APK 파일을 휴대폰으로 전송**
   ```bash
   adb push app/build/outputs/apk/release/app-release.apk /sdcard/Download/
   ```

2. **휴대폰에서 설치**
   - 파일 관리자 앱 열기
   - 다운로드 폴더로 이동
   - `app-release.apk` 파일 터치
   - "알 수 없는 소스로부터 앱 설치" 허용 (필요시)
   - 설치 버튼 터치

#### 방법 3: 이메일 또는 클라우드 전송

1. APK 파일을 이메일 첨부 또는 클라우드 스토리지에 업로드
2. 휴대폰에서 파일 다운로드
3. 파일 관리자에서 APK 파일 실행하여 설치

### 설치 후 확인

- 앱 서랍에서 "즐비드" 앱 아이콘 확인
- 앱 실행하여 정상 작동 확인
- 필요시 권한 설정 확인

### 보안 설정

Android 8.0 이상에서는 알 수 없는 소스에서 앱 설치를 허용해야 합니다:
- 설정 > 보안 > 알 수 없는 소스에서 설치 허용
- 또는 설정 > 보안 > 알 수 없는 앱 설치 > 파일 관리자 허용

## App Link 문제 해결

### 문제 상황
- **개발 빌드**: App Link가 정상 작동 (외부 브라우저 → 앱으로 복귀)
- **프로덕션 빌드**: App Link가 작동하지 않음 (웹에서 로그인 완료, 앱으로 복귀 안됨)

### 원인 분석
1. **서명 키 차이**: 개발 빌드와 프로덕션 빌드의 서명 키가 다름
2. **Digital Asset Links 미설정**: zbd.kr 서버에 프로덕션 서명 키 정보가 없음
3. **App Link 자동 검증 실패**: `android:autoVerify="true"` 설정이 실패

### 해결 방법

#### 1. 즉시 해결 (모든 기기에 적용)
AndroidManifest.xml에서 `android:autoVerify="false"` 명시적 설정:
```xml
<intent-filter android:autoVerify="false">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="https"
        android:host="zbd.kr"
        android:pathPrefix="/service/auth/google_callback" />
</intent-filter>
```

**장점**: 모든 기기에서 작동, 추가 설정 불필요  
**단점**: 사용자가 앱 선택 다이얼로그에서 수동으로 앱을 선택해야 함

#### 2. 근본적 해결 (서버 설정 필요)
zbd.kr 서버에 Digital Asset Links 파일 생성:

**파일 경로**: `https://zbd.kr/.well-known/assetlinks.json`

**내용**:
```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "kr.zbd.android",
    "sha256_cert_fingerprints": ["F3:44:36:8A:F7:3D:2E:E6:94:D8:38:23:41:76:78:6E:91:C4:99:BA:A3:65:FC:7B:8C:C3:01:AD:28:DD:A5:4B"]
  }
}]
```

#### 3. 앱 링크 상태 확인
앱 설치 후 App Link 상태 확인:
```bash
adb shell am start -W -a android.intent.action.VIEW -d "https://zbd.kr/service/auth/google_callback" kr.zbd.android
```

#### 4. 로그 확인
앱 실행 중 로그 확인:
```bash
adb logcat -s MainActivity
```

#### 5. 도메인 검증 상태 확인 및 수동 활성화

**도메인 검증 상태 확인:**
```bash
adb shell dumpsys package kr.zbd.android | grep -A 10 -B 5 "Domain verification"
```

**zbd.kr 도메인이 "Disabled" 상태인 경우 수동 활성화:**
```bash
adb shell pm set-app-links-user-selection --package kr.zbd.android --user 0 true zbd.kr
```

**App Link 테스트:**
```bash
adb shell am start -W -a android.intent.action.VIEW -d "https://zbd.kr/service/auth/google_callback" kr.zbd.android
```

### 🚨 중요 사항

#### ADB 명령어 vs 앱 코드 수정

**ADB 명령어 해결 방법**:
- ✅ 현재 기기에서만 작동
- ❌ 다른 기기에 APK 설치 시 동일한 문제 발생
- ❌ 각 기기마다 수동 설정 필요

**앱 코드 수정 방법**:
- ✅ 모든 기기에서 작동
- ✅ APK 배포 시 추가 설정 불필요
- ⚠️ 사용자가 앱 선택 다이얼로그에서 수동 선택 필요

#### 권장사항
1. **테스트 환경**: ADB 명령어로 빠른 테스트
2. **프로덕션 배포**: 앱 코드 수정 (`android:autoVerify="false"`)
3. **최적 환경**: 서버에 Digital Asset Links 파일 설정

### 프로덕션 서명 키 정보
- **SHA256 지문**: `F3:44:36:8A:F7:3D:2E:E6:94:D8:38:23:41:76:78:6E:91:C4:99:BA:A3:65:FC:7B:8C:C3:01:AD:28:DD:A5:4B`
- **패키지명**: `kr.zbd.android`

## 🔗 App Link 설정 가이드

### 📱 android:autoVerify 설정 차이점

#### `android:autoVerify="true"` (권장)
- **동작**: Google이 자동으로 Digital Asset Links 파일을 검증
- **성공 시**: 웹에서 앱으로 **자동 이동** (사용자 선택 불필요)
- **실패 시**: 웹 브라우저에 머물거나 앱 선택 다이얼로그 표시
- **조건**: 서버에 올바른 `assetlinks.json` 파일 필수

#### `android:autoVerify="false"`
- **동작**: 자동 검증 건너뛰기
- **결과**: 항상 **앱 선택 다이얼로그** 표시
- **장점**: 서버 설정과 무관하게 동작
- **단점**: 사용자가 매번 앱을 수동 선택해야 함

### 🌐 Digital Asset Links 파일 생성

#### 1. SHA256 지문 확인
프로덕션 키스토어의 SHA256 지문을 확인합니다:
```bash
keytool -list -v -keystore keys/release.keystore -alias my-app-alias | grep "SHA256:"
```

#### 2. assetlinks.json 파일 생성
서버의 `https://도메인/.well-known/assetlinks.json` 경로에 다음 내용으로 파일을 생성합니다:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "kr.zbd.android",
    "sha256_cert_fingerprints": [
      "F3:44:36:8A:F7:3D:2E:E6:94:D8:38:23:41:76:78:6E:91:C4:99:BA:A3:65:FC:7B:8C:C3:01:AD:28:DD:A5:4B"
    ]
  }
}]
```

#### 3. 파일 업로드 확인
```bash
curl -s https://zbd.kr/.well-known/assetlinks.json | jq .
```

#### 4. nginx 캐시 비활성화 (권장)
```nginx
location /.well-known/ {
    expires -1;  # 캐시 비활성화
    alias /srv/octopus-fe-service/.well-known/;
}
```

### 🔍 Google API 캐시 상태 확인

#### 1. Google Digital Asset Links API로 검증
```bash
curl -s "https://digitalassetlinks.googleapis.com/v1/statements:list?source.web.site=https://zbd.kr&relation=delegate_permission/common.handle_all_urls" | jq .
```

#### 2. 올바른 응답 예시
```json
{
  "statements": [{
    "source": {
      "web": { "site": "https://zbd.kr." }
    },
    "relation": "delegate_permission/common.handle_all_urls",
    "target": {
      "androidApp": {
        "packageName": "kr.zbd.android",
        "certificate": {
          "sha256Fingerprint": "F3:44:36:8A:F7:3D:2E:E6:94:D8:38:23:41:76:78:6E:91:C4:99:BA:A3:65:FC:7B:8C:C3:01:AD:28:DD:A5:4B"
        }
      }
    }
  }],
  "maxAge": "3600s"
}
```

#### 3. 캐시 갱신 대기 시간
- **일반적**: 1-4시간
- **최대**: 24시간
- **확인 방법**: `maxAge` 값이 갱신되고 올바른 지문이 표시될 때까지 주기적 확인

#### 4. 앱에서 도메인 검증 상태 확인
```bash
adb shell dumpsys package kr.zbd.android | grep -A 15 "Domain verification"
```

정상 상태:
```
Domain verification state:
  zbd.kr: 1  # 성공 (1024는 실패)
Selection state:
  Enabled:
    zbd.kr
```

#### 5. 강제 재검증
Google 캐시가 갱신된 후:
```bash
adb shell pm verify-app-links --re-verify kr.zbd.android
```

### ⚠️ 주의사항

1. **서버 파일 업데이트 후**: Google 캐시 갱신까지 시간 소요
2. **테스트 환경**: `android:autoVerify="false"`로 테스트 후 `true`로 변경
3. **프로덕션 배포**: Google 캐시 상태 확인 후 배포 권장
4. **키스토어 변경 시**: 반드시 `assetlinks.json` 파일도 함께 업데이트