# hybrid-app

## 목차<br>
[Toast](#Toast)<br>
[Snackbar](#Snackbar)<br>
[Dialog](#Dialog)<br>
[Network](#Network)<br>
[Permission](#Permission)<br>
[Photo](#Photo)<br>
[Location](#Location)<br>
[Notification](#Location)<br>
[BioAuthentication](#BioAuthentication)<br>
[App ID](#App-ID)<br>
[Device ID](#Device-ID)<br>

## Toast

#### 1 ) 함수
- fun showShortText(message: String)<br>
: 짧은 토스트 메시지 출력

- fun showLongText(message: String)<br>
: 긴 토스트 메시지 출력

#### 2 ) 사용법
```kotlin
...
Toast.showShortText(message)
Toast.showLongText(message)
...
```

## Snackbar

#### 1 ) 함수
- fun showShortText(view: View, message: String)<br>
: 짧은 스낵바 메시지 출력

- fun showLongText(view: View, message: String)<br>
: 긴 스낵바 메시지 출력

#### 2 ) 사용법
```kotlin
...
Snackbar.showShortText(App.activity.findViewById(R.id.constraintLayout), message)
Snackbar.showLongText(App.activity.findViewById(R.id.constraintLayout), message)
...
```

## Dialog

#### 1 ) 함수
- show(title: String?, message: String?, positiveButtonText: String?, neutralButtonText: String?,
             negativeButtonText: String?, positiveListener: DialogInterface.OnClickListener?,
             neutralListener: DialogInterface.OnClickListener?, negativeListener: DialogInterface.OnClickListener?,
             cancelListener: () -> Unit?)<br>
: 다이얼로그 출력

#### 2 ) 사용법
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
From Web >
순서
basic
destuctive
cancel

## Network
#### 1 ) 함수
- fun getStatus(context: Context): Int<br>
: 네트워크 상태를 확인<br>
return:<br>
0 : 연결되지 않음<br>
1 : 데이터에 연결됨<br>
2 : 와이파이에 연결됨<br>

- private fun getConnectivityManager(context: Context): ConnectivityManager<br>
: ConnectivityManager를 생성한다

#### 2 ) 사용법
```kotlin
...
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
```

## Permission
#### 1 ) 함수
- fun existsPermission(permissionName: String): Boolean<br>
: permissionName이 있는지 없는지 확인

- fun existAllPermission(permissions: Array<out String>): Boolean<br>
: permissions가 모두 있는지 확인

- private fun isDenialPermission(permissionName: String): Boolean<br>
: permissionName이 거절되었는지 확인

- fun checkDangerousPermissions(permissions: Array<out String>, permissionCode: Int)<br>
: 위험 권한이 없을 경우

- private fun existsDenialPermission(permissions: Array<out String>): Boolean<br>
: 거절한 권한이 하나라도 있는지 확인

- private fun getPermissionsToRequest(permissions: Array<out String>): Array<out String><br>
: 요청이 필요한 권한들을 반환

- fun requestPermissions(permissions: Array<out String>, requestCode: Int)<br>
: 다수 권한 요청

- private fun requestAppSettingsIntent()<br>
: 앱 설정 화면으로 이동

- private fun getDialogMessage(permissionCode: Int): String<br>
: 권한 다이얼로그 메시지

- fun existsReceiveActivity(intent: Intent, packageManager: PackageManager): Boolean<br>
 : 암시적 인텐트를 받을 수 있는 앱이 있는지 확인
 
#### 2 ) 사용법
```kotlin
if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
// 
// 카메라 앱이 있는지 확인
if(Utils.existsReceiveActivity(cameraIntent, packageManager)) {
if(isWidthRatio != null) { // 디바이스 기준으로 resize
basicActivity.cameraDeviceAction = action
basicActivity.isWidthRatio = isWidthRatio
basicActivity.startActivityForResult(cameraIntent,
Constants.CAMERA_DEVICE_RATIO_REQ_CODE)
} else { // 이미지 기준으로 resize
basicActivity.cameraAction = action
basicActivity.startActivityForResult(cameraIntent,
Constants.CAMERA_RATIO_REQ_CODE)
}
} else {
Constants.LOGE(Constants.LOG_MSG_CAMERA)
action.resolveVoid()
}
} else { // 권한이 없을 경우
action?.resolveVoid()
Utils.checkDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
Constants.PERM_CAMERA_REQ_CODE)
}
```

## Photo

fun takeCameraIntent(): Intent

fun startScan()

fun takeSinglePhotoIntent(): Intent

fun takeMultiplePhotosIntent(): Intent

fun existsReceiveActivity(intent: Intent, packageManager: PackageManager): Boolean

internal fun getBase64FromUri(uri: Uri): String

private fun getBitmapFromUri(uri: Uri): Bitmap

internal fun getBase64FromBitmap(bitmap: Bitmap): String

fun takeAppSettingsIntent(): Intent

## Notification

## BioAuthentication
필요 권한 > USE_BIOMETRIC(Protection level: normal)
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.hybridapp">
    ...
    <!-- Biometric -->
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />
    ...
```

의존성 추가 >
```gradle
// Biometric
dependencies {
    ...
    implementation 'androidx.biometric:biometric:1.0.1'
    ...
}
```

참고 사이트 >
- https://dev.to/ranilch/securing-data-with-biometricprompt-35mo
## App ID
## Device ID
