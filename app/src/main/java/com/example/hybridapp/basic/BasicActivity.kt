package com.example.hybridapp.basic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.module.*

@SuppressLint("Registered")
open class BasicActivity: AppCompatActivity() {

    /** 액션 인스턴스 */
    var cameraDeviceAction: FlexAction? = null
    var cameraAction: FlexAction? = null
    var photoDeviceAction: FlexAction? = null
    var photoAction: FlexAction? = null
    var multiplePhotoDeviceAction: FlexAction? = null
    var multiplePhotoAction: FlexAction? = null
    var qrCodeScanAction: FlexAction? = null
    var sendSmsAction: FlexAction? = null
    var locationAction: FlexAction? = null
    var authAction: FlexAction? = null
    var fileAction: FlexAction? = null
    var popUpAction: FlexAction? = null

    /** 이미지 리사이징 정보 */
    var ratio: Double? = null
    var isWidthRatio: Boolean? = null

    var downloadId: Long? = null

    /** SEND SMS */
    var phoneNumber: String? = null
    var smsMessage: String? = null

    var fileUrl: String? = null

    /** 팝업 관련 */
    lateinit var backgroundView: View
    lateinit var popUpCloseButton: Button

    /** 뒤로가기 두 번 방지 Boolean */
    var isPressedTwice: Boolean = false

    /** Module */
    var toast: Toast? = null
    var qrCode: QRCode? = null
    var camera: Camera? = null
    var photo: Photo? = null
    var location: Location? = null
    var sms: SMS? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toast = Toast()
        qrCode = QRCode()
        camera = Camera()
        photo = Photo()
        location = Location()
        sms = SMS()
    }
}