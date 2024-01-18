package com.omif.gsha.model

import android.widget.TextView

class Vitals {
    var weight: Double? = 0.0
    var height: Double? = 0.0
    var bpSys: Int? = 0
    var bpDia: Int? = 0
    var temp: Int? = 0
    var sugar: Int? = 0
    var pulseRate: Int? = 0
    var spO2: Int? = 0
    var uid : String? = null
    var date: String?=null

    constructor(){}

    constructor(weight: Double?, height:Double?, bpDia: Int?,bpSys:Int?, temp:Int?, sugar: Int?, pulseRate: Int?, spO2: Int?, uid: String?, date: String? ){
        this.weight = weight
        this.height = height
        this.bpDia = bpDia
        this.bpSys = bpSys
        this.temp = temp
        this.sugar = sugar
        this.pulseRate = pulseRate
        this.spO2 = spO2
        this.uid = uid
        this.date = date
    }
}