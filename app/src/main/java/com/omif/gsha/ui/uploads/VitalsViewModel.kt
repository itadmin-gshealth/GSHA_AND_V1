package com.omif.gsha.ui.uploads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VitalsViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Vitals"
    }
    val text: LiveData<String> = _text
}