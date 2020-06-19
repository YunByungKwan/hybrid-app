# hybrid-app

## 목차<br>
[Native Activity](#Native-Activity)<br>
[Toast Module](#Toast-Module)<br>
[Snackbar Module](#Snackbar-Module)<br>
[Dialog Module](#Dialog-Module)<br>
[Network Module](#Network-Module)<br>
[Permission Module](#Permission-Module)<br>
[Camera Module](#Camera-Module)<br>
[QR Code Module](QR-Code-Module)<br>
[Photo Module](#Photo-Module)<br>
[Location Module](#Location-Module)<br>
[Notification Module](#Location-Module)<br>
[FCM Module](#FCM-Module)<br>
[SMS Module](#SMS-Module)<br>
[BioAuthentication Module](#BioAuthentication-Module)<br>
[App ID Module](#App-ID-Module)<br>
[Device ID Module](#Device-ID-Module)<br>

## Native Activity

#### Android JNI 연동 >
- CMake 사용<br>
  - https://velog.io/@ybg7955/AndroidKotlin-NativeActivity%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EB%B2%95
- ndk-build 사용<br>
  - https://re-build.tistory.com/7
  - https://dev.re.kr/67
  - https://thepassion.tistory.com/332

#### 함수 >
- size_t responseWriter(char*, size_t, size_t, std::string*)<br>
: cURL을 이용하여 http통신 후 response를 받는 callback함수 

- bool isCorrectKeyHash(const char*)<br>
: 서버에 해쉬값을 전송

- bool isHttpConnected(long)<br>
: 서버로 받은 code값으로 http통신이 잘 되었는지 판별

- bool canRunSuCommand()<br>
: Su 명령어가 가능한지 판별

- bool existSuspectedRootingFiles()<br>
: 루팅 의심 파일이 있는지 체크

- char* getSignature(JNIEnv*, jobject)<br>
: 해쉬값을 생성 후 반환

- void startActivityAndFinish(JNIEnv*, jobject, const char*)<br>
: 인텐트를 사용하여 해당 액티비티로 이동

#### 사용법 >
```c
...
if(canRunSuCommand() || existSuspectedRootingFiles()) {
    exit(0);
}
...
// Get hash
jobject context = state->activity->clazz;
JNIEnv *env;
state->activity->vm->AttachCurrentThread(&env, NULL);
const char* hash = getSignature(env, context);
...
if(!isCorrectKeyHash(hash)) {
    exit(0);
}
...
startActivityAndFinish(env, context, "com.example.hybridapp.MainActivity");
...
```

## Toast Module

#### 함수 >
- fun showShortText(message: String)<br>
: 짧은 토스트 메시지 출력

- fun showLongText(message: String)<br>
: 긴 토스트 메시지 출력

#### 사용법 >

```kotlin
...
val message = "Toast message"

Toast.showShortText(message)
Toast.showLongText(message)
...
```


## Snackbar Module

#### 함수 >
- fun showShortText(view: View, message: String)<br>
: 짧은 스낵바 메시지 출력

- fun showLongText(view: View, message: String)<br>
: 긴 스낵바 메시지 출력

#### 사용법 >
```kotlin
...
val message = "Snackbar message"

Snackbar.showShortText(App.activity.findViewById(R.id.constraintLayout), message)
Snackbar.showLongText(App.activity.findViewById(R.id.constraintLayout), message)
...
```


## Dialog Module

#### 함수 >
- show(title: String?, message: String?, positiveButtonText: String?, neutralButtonText: String?,
             negativeButtonText: String?, positiveListener: DialogInterface.OnClickListener?,
             neutralListener: DialogInterface.OnClickListener?, negativeListener: DialogInterface.OnClickListener?,
             cancelListener: () -> Unit?)<br>
: 다이얼로그 출력

#### 사용법 >
```kotlin
...
Dialog.show(Constants.DIAL_TITLE,
                getDialogMessage(permissionCode),
                Constants.DIAL_POS,
                null,
                Constants.DIAL_NEG,
                DialogInterface.OnClickListener { _, _ ->
                    requestAppSettingsIntent()
                },
                null,
                null,
                {})
...
```


## Network Module

#### 필요 권한 > ACCESS_NETWORK_STATE(Protection level: normal)
```xml
<manifest>
    ...
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    ...
```
#### 함수 >
- fun getStatus(context: Context): Int<br>
: 네트워크 상태를 확인<br>
return:<br>
0 : 연결되지 않음<br>
1 : 데이터에 연결됨<br>
2 : 와이파이에 연결됨<br>

- private fun getConnectivityManager(context: Context): ConnectivityManager<br>
: ConnectivityManager를 생성

#### 사용법 >
```kotlin
...
val manager = getConnectivityManager(context)
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = manager.activeNetwork ?: return Constants.NET_STAT_DISCONNECTED
        val capabilities = manager.getNetworkCapabilities(network)
        when(Network.getStatus(App.activity)) {
            0 -> {
                // task...
            }
            1 -> {
                // task...
            }
            2 -> {
                // task...
            }
        }
        ...
    ...
```


## Permission Module

#### 위험 권한 체크

if(필요 권한이 모두 존재할 때) {<br>
--if(암시적 인텐트를 받을 수 있는 앱이 존재할 때) {<br>
----// task...<br>
--} else {<br>
----// 인텐트를 받는 앱이 없을 경우<br>
--}<br>
} else {<br>
--// 필요 권한이 하나라도 존재하지 않는 경우<br>
}<br>

#### 함수 >
- fun existsPermission(permissionName: String): Boolean<br>
: 해당 권한이 있는지 없는지 확인

- fun existAllPermission(permissions: Array<out String>): Boolean<br>
: 해당 권한들이 모두 있는지 확인

- private fun isDenialPermission(permissionName: String): Boolean<br>
: 해당 권한이 거절되었는지 확인

- fun checkAbsentPerms(permissions: Array<out String>, permissionCode: Int)<br>
: 위험 권한이 없을 경우<br>
  --if(권한이 거절되었을 때) {<br>
  ----// task...<br>
  --} else {<br>
  ----// task...<br>
  --}

- private fun existsDenialPermission(permissions: Array<out String>): Boolean<br>
: 거절한 권한이 하나라도 있는지 확인

- private fun getPermissionsToRequest(permissions: Array<out String>): Array<out String><br>
: 요청이 필요한 권한들을 반환

- fun requestPermissions(permissions: Array<out String>, requestCode: Int)<br>
: 다수의 권한을 요청

- private fun requestAppSettingsIntent()<br>
: 앱 설정 화면으로 이동

- private fun getDialogMessage(permissionCode: Int): String<br>
: 권한 다이얼로그 메시지를 얻음

- fun existsReceiveActivity(intent: Intent, packageManager: PackageManager): Boolean<br>
 : 암시적 인텐트를 받을 수 있는 앱이 있는지 확인
 
#### 사용법 >
```kotlin
...
/** 위험 권한 요청시 프로세스 */
if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
    // 필요 권한이 모두 존재하는 경우
    if(Utils.existsReceiveActivity(cameraIntent, packageManager)) {
        // 인텐트를 받는 앱이 존재할 경우
    } else {
        // 인텐트를 받는 앱이 없을 경우
    }
} else {
    // 필요 권한이 하나라도 존재하지 않는 경우
}
...
```


## Camera Module

#### 필요 권한 > CAMERA(Protection level: dangerous)
```xml
<manifest>
    <uses-permission android:name="android.permission.CAMERA"/>
...
```

#### 의존성 추가 >
```gradle
dependencies {
    ...
    implementation 'androidx.exifinterface:exifinterface:1.2.0'
    ...
}
```

#### 함수
- fun request(action: FlexAction?, ratio: Double?, isWidthRatio: Boolean?)
: 카메라 앱을 호출

#### 사용법
```kotlin
...
Camera.request(cameraAction, ratio, null)
...
```

## QR Code Module

#### 필요 권한 > CAMERA(Protection level: dangerous)
```xml
<manifest>
    ...
    <uses-sdk tools:overrideLibrary="com.google.zxing.client.android" />
    <uses-permission android:name="android.permission.CAMERA"/>
    ...
    <application
        android:hardwareAccelerated="true"
        ...
    ...
...
```

#### 의존성 추가 >
```gradle
dependencies {
    ...
    implementation 'com.google.zxing:core:3.3.0' // For Android SDK versions < 24
    implementation('com.journeyapps:zxing-android-embedded:3.6.0') { transitive = false }
    ...
}
```

#### 함수 >
- fun startScan(action: FlexAction?)

#### 사용법 >
```kotlin
...
QRCode.startScan()
...
...
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    ...
    IntentIntegrator.REQUEST_CODE -> {
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null) {
            if(result.contents != null) {
                Toast.makeText(this, result.contents, Toast.LENGTH_SHORT)
            } else {
                Toast.makeText(this, "Result is null", Toast.LENGTH_SHORT)
            }
        } else {
            // result fail...
        }
    }
    ...
...
```
#### References >
- https://github.com/journeyapps/zxing-android-embedded

## Photo Module

#### 필요 권한 > CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE
```xml
<manifest>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
...
```

#### 의존성 추가
```gradle
dependencies {
    ...
    implementation 'androidx.exifinterface:exifinterface:1.2.0'
    ...
}
```

#### 함수
- fun requestImage(action: FlexAction?, ratio: Double?, isWidthRatio: Boolean?)<br>
: 갤러리 앱을 호출 (1장)

- fun requestMultipleImages(action: FlexAction?, ratio: Double, isWidthRatio: Boolean?)<br>
: 갤러리 앱을 호출 (여러 장)

- private fun getSinglePhotoIntent(): Intent<br>
: 갤러리 인텐트를 반환 (1장일 경우)

- private fun getMultiplePhotosIntent(): Intent<br>
: 갤러리 인텐트를 반환 (여러 장일 경우)

- fun convertUriToResizingBase64(imageUri: Uri?, ratio: Double?, isWidthRatio: Boolean?): String<br>
: Uri -> Bitmap -> Resizing -> Base64로 변환

- fun getBase64FromUri(uri: Uri): String<br>
: Uri -> Bitmap -> Base64로 변환

- private fun getBitmapFromUri(uri: Uri): Bitmap?<br>
: Uri -> Bitmap로 변환

- private fun getFilePathFromUri(uri: Uri): String<br>
: Uri -> File Path로 변환

- private fun getDegreesFromPath(filePath: String): Int<br>
: 파일 경로에서 이미지의 사진 정보를 얻음

- private fun getDegreesFromExifOrientation(orientation: Int): Int<br>
: ExifOrientation으로부터 Degree를 얻음

- private fun createBitmapFromFilePath(filePath: String): Bitmap?<br>
: File Path로부터 Bitmap을 생성

- private fun rotateBitmap(bitmap: Bitmap?, degree: Int): Bitmap?<br>
- 비트맵을 회전시킴

- fun getBase64FromBitmap(bitmap: Bitmap): String<br>
: Bitmap -> Base64로 변환

- private fun resizeBitmapByDeviceRatio(bitmap: Bitmap, ratio: Double, isWidthRatio: Boolean?): Bitmap<br>
: 화면 크기의 비례하게 이미지를 리사이징함

- private fun resizeBitmapByRatio(bitmap: Bitmap, ratio: Double): Bitmap <br>
: 이미지 자체 비율에 따라 리사이징함

#### 사용법
```kotlin
...
Camera.request(cameraDeviceAction, ratio, isWidthRatio)
Photo.requestImage(photoDeviceAction, ratio, isWidthRatio)
Photo.requestMultipleImages(multiplePhotoDeviceAction, ratio!!, isWidthRatio!!)
...
```

## Location Module

#### 필요 권한 > ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION
```xml
<manifest>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
...
```

#### 의존성 추가
```gradle
dependencies {
    ...
    implementation 'com.google.android.gms:play-services-maps:17.0.0'
    implementation 'com.google.android.gms:play-services-location:17.0.0'
    ...
}
```

#### 함수
- fun getCurrent(action: FlexAction?)<br>
: 현재 위치의 위도, 경도를 가져옴

- private fun isLocationEnabled(context: Context): Boolean<br>
: GPS 기능을 사용할 수 있는지 없는지 판별

- private fun getLocationRequest(): LocationRequest<br>
: LocationRequest를 생성 후 반환

- LocationCallback() 객체 {<br>
: onLocationResult()를 오버라이드해야 함

#### 사용법 >
```kotlin
...
Location.getCurrentLatAndLot()
...
```

## Notification Module

#### 함수 >
- fun createChannel(channelId: String, name: String, description: String, importance: Int, showBadge: Boolean)<br>
: 알림 채널 생성 (Android 8.0(API 레벨 26, O)이상부터 필수 생성해야 함)

- private fun getNotificationManager(context: Context): NotificationManager<br>
: Notification manager 생성 후 반환

- fun create(channelId: String, notificationId: Int, title: String, message: String, importance: Int, pendingIntent: PendingIntent)<br>
: 알림 생성

#### 사용법 >
```kotlin
...
/** 알림 채널 생성 */
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    Notification.createChannel(channelId, channelName, description, importance, true)
} else {
    // 알림 채널 생성할 필요 없음
}
...
/** 알림 생성 */
Notification.create(channelId, id, title, message, Constants.NOTI_HIGH, pendingIntent)
...
```

## FCM Module

#### 의존성 추가 >
```gradle
dependencies {
    ...
    implementation 'com.google.firebase:firebase-analytics:17.4.3'
    implementation 'com.google.firebase:firebase-messaging:20.2.0'
    ...
}
```

#### 클래스 >
```kotlin
class FCM: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // task...
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // task...
    }
}
```

## SMS Module

#### 필요 권한 > SEND_SMS(Protection level: dangerous)
```xml
<manifest>
    ...
    <!-- SMS를 보낼 경우 추가 -->
    <uses-permission android:name="android.permission.SEND_SMS"/>
    ...
```
#### 클래스 >
- class SMSReceiver: BroadcastReceiver()<br>
: 브로드캐스트 리시버를 상속받는 리시버 클래스

#### 함수 >
- fun registerReceiver(receiver: SMSReceiver?)<br>
: SMS 리시버를 등록

- fun unregisterReceiver(receiver: SMSReceiver?)<br>
: SMS 리시버를 해제

- fun sendMessage(phoneNumber: String, message: String): String<br>
: 해당 스마트폰 번호로 문자 메시지를 보냄

- fun receiveMessage()<br>
: 아래와 같은 형식의 문자 메시지가 올 경우 수신<br>
Ex )<br>
<#><br>
테스트입니다.<br>
amvosl3kf/u<br>

## BioAuthentication Module

#### 필요 권한 > USE_BIOMETRIC(Protection level: normal)
```xml
<manifest>
    ...
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />
    ...
```

#### 의존성 추가 >
```gradle
// Biometric
dependencies {
    ...
    implementation 'androidx.biometric:biometric:1.0.1'
    ...
}
```

#### References >
- https://dev.to/ranilch/securing-data-with-biometricprompt-35mo


## App ID Module

#### 함수 >
- fun getAppId(): String<br>
: 앱의 초기 UUID를 생성

#### 사용법 >
```kotlin
...
Utils.getAppId()
...
```

## Device ID Module

#### 함수 >
- fun getDeviceId(context: Context): String<br>
: Android id를 가져옴

#### 사용법 >
```kotlin
...
Utils.getDeviceId(App.INSTANCE)
...
```
