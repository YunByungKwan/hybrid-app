package com.example.hybridapp.basic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
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
    var qrInstance: QRCode? = null
    var cameraInstance: Camera? = null
    var photoInstance: Photo? = null
    var locInstance: Location? = null
    var smsInstance: SMS? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        qrInstance = QRCode(this)
        cameraInstance = Camera(this)
        photoInstance = Photo(this)
        locInstance = Location(this)
        smsInstance = SMS(this)
    }
}