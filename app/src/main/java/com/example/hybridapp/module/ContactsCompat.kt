package com.example.hybridapp.module

import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.hybridapp.MainActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils

class ContactsCompat(private val basicAct: MainActivity) {

    /** 연락처에서 선택한 사람의 이름 및 핸드폰 번호를 불러옴 */
    fun getNameAndNumberFromContacts() {
        val permission = Constants.PERM_READ_CONTACTS
        if(Utils.existsPermission(permission)) {
            dispatchContactsIntent()
        } else {
            permissionResult.launch(permission)
        }
    }

    /** 주소록 앱을 불러옴 */
    private fun dispatchContactsIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        if(Utils.existsReceiveActivity(intent, basicAct.packageManager)) {
            activityResult.launch(intent)
        } else {
            // 수신받을 앱 없음
        }
    }

    /** onActivityResult */
    private val activityResult = basicAct.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        if(Utils.resultOk(resultCode)) {
            var cursor: Cursor?

            data.let {
                cursor = basicAct.contentResolver.query(
                    it!!.data!!,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                        ),
                    null,
                    null,
                    null)
            }

            cursor.let {
                it!!.moveToFirst()
                val userName = it.getString(0)
                val phoneNumber = it.getString(1)
                ToastCompat.showShortToast("PhoneNumber: $phoneNumber")
                it.close()
            }
        }
    }

    /** onRequestPermissionResult */
    private val permissionResult = basicAct.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted) {
            getNameAndNumberFromContacts()
        } else {
            // 권한이 거부됨
        }
    }

}