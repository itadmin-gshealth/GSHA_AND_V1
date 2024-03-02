package com.omif.gsha.model

import java.util.Date

class Prescription {
    var medicine: String? = null
    var doctorName: String? = null
    var doctorRegNo: String? = null
    var doctorId: String? = null
    var patientName: String? = null
    var patientId: String? = null
    var department: String? = null
    var diagnosis: String? = null
    var followUpReq: Boolean? = false
    var url: String? = null
    var date: String? = null

    constructor(){}

    constructor(medicine: String?, doctorName: String?, doctorRegNo: String?,doctorId: String?, patientName: String?, patientId: String?,department:String?, diagnosis:String?, followUpReq:Boolean, url:String?, date: String?){
        this.medicine = medicine
        this.doctorName = doctorName
        this.doctorRegNo = doctorRegNo
        this.doctorId = doctorId
        this.patientName = patientName
        this.patientId = patientId
        this.department = department
        this.diagnosis = diagnosis
        this.followUpReq = followUpReq
        this.url = url
        this.date = date
    }

}