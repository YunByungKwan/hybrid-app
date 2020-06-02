# hybrid-app

## Dialog
From Web >
순서
basic
destuctive
cancel

#### 1. Toast

fun showShortText(message: String)

fun showLongText(message: String)

#### 2. Snackbar

fun showShortSnackbar(view: View, message: String)

fun showLongSnackbar(view: View, message: String)

#### 3. Dialog

fun show(title: String, message: String)

fun show(title: String, message: String, positiveButtonText: String)

fun show(title: String, message: String, positiveButtonText: String, negativeButtonText: String)

fun show(title: String, message: String, positiveButtonText: String, negativeButtonText: String,
    pListener: DialogInterface.OnClickListener?, nListener: DialogInterface.OnClickListener?)

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

internal fun convertUriToBase64(uri: Uri): String

private fun convertUriToBitmap(uri: Uri): Bitmap

internal fun convertBitmapToBase64(bitmap: Bitmap): String

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
