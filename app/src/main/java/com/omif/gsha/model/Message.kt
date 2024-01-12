package com.omif.gsha.model

import java.util.Date

class Message {
    var message: String? = null
    var senderId: String? = null
    var receiverId: String? = null
    var messageDate: String? = null

    constructor(){}

    constructor(message: String?, senderId: String?, receiverId: String?, date: String?){
        this.message = message
        this.senderId = senderId
        this.receiverId = receiverId
        this.messageDate = date
    }

}