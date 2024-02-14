package com.omif.gsha.model

class User {

    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var phoneNumber: String? = null
    var department: String? = null
    var gender: String? = null
    var age: Int = 0
    var qual: String? =null
    var imageLink: String? = null
    var uType: Int = 0
    var parentId: String? = null
    var internal: Int = 0

    constructor(){}

    constructor(name: String?, email:String?, uid: String?,phoneNumber:String?, department:String?, gender:String?, age:Int, qual: String?, imageLink: String?, uType: Int, parentId: String?, internal:Int){
        this.name = name
        this.email = email
        this.uid = uid
        this.phoneNumber = phoneNumber
        this.department = department
        this.gender = gender
        this.age = age
        this.qual = qual
        this.imageLink = imageLink
        this.uType = uType
        this.parentId = parentId
        this.internal = internal
    }

}