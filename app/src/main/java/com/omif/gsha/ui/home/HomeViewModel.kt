package com.omif.gsha.ui.home

import android.graphics.Rect
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Skip the travel, consult our Doctors online"
    }
    val text: LiveData<String> = _text


}