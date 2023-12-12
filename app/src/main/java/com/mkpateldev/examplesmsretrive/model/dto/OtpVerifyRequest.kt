package com.mkpateldev.examplesmsretrive.model.dto

data class OtpVerifyRequest(val mobile: String,
                            val isOTPverified: Boolean,
                            val fcmid:String,
                            val currentAPKversion:String,
                            val phoneOSversion:String,
                            val userDeviceName:String,
                            val deviceId:String
                        )