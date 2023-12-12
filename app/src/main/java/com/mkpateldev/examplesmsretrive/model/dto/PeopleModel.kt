package com.mkpateldev.examplesmsretrive.model.dto

import com.google.gson.annotations.SerializedName

class PeopleModel {
    @SerializedName("PeopleID")
    var peopleID = 0

    @SerializedName("CompanyId")
    var companyId = 0

    @SerializedName("WarehouseId")
    var warehouseId = 0

   /* @SerializedName("WarehouseName")
    var warehouseName: String = ""*/

    @SerializedName("PeopleFirstName")
    var peopleFirstName: String = ""

    @SerializedName("PeopleLastName")
    var peopleLastName: String = ""

    @SerializedName("DOB")
    var dOB: String = ""

    @SerializedName("Email")
    var email: String = ""

    @SerializedName("DisplayName")
    var displayName: String = ""

    @SerializedName("Cityid")
    var cityid = 0

    @SerializedName("city")
    var city: String = ""

    @SerializedName("Mobile")
    var mobile: String = ""

    @SerializedName("Password")
    var password: String = ""

    @SerializedName("Department")
    var department: String = ""

    @SerializedName("ImageUrl")
    var imageUrl: String = ""

    @SerializedName("Active")
    var active = false

    @SerializedName("CreatedDate")
    var createdDate: String = ""

    @SerializedName("Skcode")
    var skcode: String = ""

    @SerializedName("OTP")
    var oTP: String = ""

    @SerializedName("Role")
    var role: String = ""

    @SerializedName("IsLocation")
    var isLocation = false

    @SerializedName("IsRecording")
    var isRecording = false

    @SerializedName("LocationTimer")
    var locationTimer = 0

    @SerializedName("Status")
    var locationStatus: String = ""

    @SerializedName("StartLat")
    var startLat = 0.0

    @SerializedName("StartLng")
    var startLng = 0.0

    @SerializedName("ProfilePic")
    var profilePic: String? = null


    @SerializedName("Empcode")
    var empCode: String = ""

    @SerializedName("WarehouseName")
    var warehouseName = ""

    @SerializedName("clusterId")
    var clusterId1 = ""

    @SerializedName("clusterName")
    var clusterName = ""

    @SerializedName("StoreId")
    var StoreId = ""

    @SerializedName("StoreName")
    var StoreName = ""

}