package com.omif.gsha.model

import java.util.Date

class Prescription {
    var medicine: String? = null
    var doctorName: String? = null
    var doctorId: String? = null
    var patientName: String? = null
    var patientId: String? = null
    var department: String? = null
    var url: String? = null
    var date: String? = null

    constructor(){}

    constructor(medicine: String?, doctorName: String?,doctorId: String?, patientName: String?, patientId: String?,department:String?, url:String?, date: String?){
        this.medicine = medicine
        this.doctorName = doctorName
        this.doctorId = doctorId
        this.patientName = patientName
        this.patientId = patientId
        this.department = department
        this.url = url
        this.date = date
    }

}