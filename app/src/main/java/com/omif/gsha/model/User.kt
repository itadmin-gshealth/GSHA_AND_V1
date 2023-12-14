package com.omif.gsha.model

class User {

    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var uType: Int = 0

    constructor(){}

    constructor(name: String?, email:String?, uid: String?, uType: Int){
        this.name = name
        this.email = email
        this.uid = uid
        this.uType = uType
    }

}