package com.omif.gsha.model

import android.widget.TextView

class EHRecord {
    var uid : String? = null
    var date: String?=null
    var link: String?=null
    var dept: String?=null
    var showId: String?=null
    constructor(){}

    constructor(uid: String?, date: String?, link: String?, dept: String? ,showId: String? ){
        this.uid = uid
        this.date = date
        this.link  = link
        this.dept = dept
        this.showId = showId

    }
}