package com.mkpateldev.examplesmsretrive.model.dto
data class OtpRequest(val mobile: String,
                      val fcmId:String,
                      val currentAPKversion:String,
                      val phoneOSversion:String,
                      val userDeviceName:String,
                      val deviceId:String
                        )
