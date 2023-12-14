package com.omif.gsha.ui.uploads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UploadsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is offers Fragment"
    }
    val text: LiveData<String> = _text
}