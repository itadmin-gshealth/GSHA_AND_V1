package com.omif.gsha.ui.members

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddMembersViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Add Your family and friends to avail One Click medical help !!"
    }
    val text: LiveData<String> = _text
}