# hybrid-app

목차<br>
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

fun showShortText(message: String)

fun showLongText(message: String)

## Snackbar
fun showShortSnackbar(view: View, message: String)

fun showLongSnackbar(view: View, message: String)

## Dialog
fun show(title: String, message: String)

fun show(title: String, message: String, positiveButtonText: String)

fun show(title: String, message: String, positiveButtonText: String, negativeButtonText: String)

fun show(title: String, message: String, positiveButtonText: String, negativeButtonText: String,
    pListener: DialogInterface.OnClickListener?, nListener: DialogInterface.OnClickListener?)

From Web >
순서
basic
destuctive
cancel
## Network
fun isConnected(): Boolean

fun getStatus(): Int
## Permission
internal fun existsPermission(permissionName: String): Boolean

internal fun isRejectPermission(permissionName: String): Boolean

fun requestPermissions(permissions: Array<out String>, requestCode: Int)

fun checkPermissions(permissions: Array<String>, requestCode: Int)

fun showDenialPermissionText()
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
