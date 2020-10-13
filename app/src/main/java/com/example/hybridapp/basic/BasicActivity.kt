package com.example.hybridapp.basic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.hybridapp.data.LogUrlRepository
import com.example.hybridapp.module.*

@SuppressLint("Registered")
open class BasicActivity: AppCompatActivity() {

    /** Repository */
    var repository: LogUrlRepository? = null

    /** 문자를 받는 Broadcast Receiver */
    var smsReceiver: SMSReceiver? = null

    /** 팝업 관련 */
    var backgroundView: View? = null

    /** 뒤로가기 두 번 방지 Boolean */
    var isPressedTwice: Boolean = false

    /** Module Instance */
    var qrCodeCompat: QRCodeCompat? = null
    var cameraCompat: CameraCompat? = null
    var photoCompat: PhotoCompat? = null
    var locationCompat: LocationCompat? = null
    var smsCompat: SmsCompat? = null
    var contactsCompat: ContactsCompat? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        qrCodeCompat = QRCodeCompat(this)
        cameraCompat = CameraCompat(this)
        photoCompat = PhotoCompat(this)
        locationCompat = LocationCompat(this)
        smsCompat = SmsCompat(this)
        contactsCompat = ContactsCompat(this)
    }
}