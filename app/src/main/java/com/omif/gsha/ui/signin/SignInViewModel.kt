package com.omif.gsha.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "SIGN-IN | REGISTER"
    }
    val text: LiveData<String> = _text
}