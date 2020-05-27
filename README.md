# hybrid-app

## Dialog

#### 1. Toast

fun showShortToast(message: String)

fun showLongToast(message: String)

#### 2. Snackbar

fun showShortSnackbar(view: View, message: String)

fun showLongSnackbar(view: View, message: String)

#### 3. Dialog

fun showDialog(title: String, message: String)

fun showDialog(title: String, message: String, positiveButtonText: String)

fun showDialog(title: String, message: String, positiveButtonText: String, negativeButtonText: String)

fun showDialog(title: String, message: String, positiveButtonText: String, negativeButtonText: String, 
    pListener: DialogInterface.OnClickListener?, nListener: DialogInterface.OnClickListener?)

## Network

fun isNetworkConnected(): Boolean

fun getNetworkStatus(): Int

## Permission

internal fun hasPermissionFor(permissionName: String): Boolean

internal fun isRejectPermission(permissionName: String): Boolean

fun requestPermissions(permissions: Array<out String>, requestCode: Int)

fun checkPermissions(permissions: Array<String>, requestCode: Int)

fun showPermissionDeniedDialog()

## Photo

fun takeCameraIntent(): Intent

fun takeQRCodeReader()

fun takeSinglePhotoIntent(): Intent

fun takeMultiplePhotosIntent(): Intent

fun existsReceiveActivity(intent: Intent, packageManager: PackageManager): Boolean

internal fun convertUriToBase64(uri: Uri): String

private fun convertUriToBitmap(uri: Uri): Bitmap

internal fun convertBitmapToBase64(bitmap: Bitmap): String

fun takeAppSettingsIntent(): Intent
