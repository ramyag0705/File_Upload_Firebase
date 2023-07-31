package com.example.fileupload

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    var storageRef: StorageReference = FirebaseStorage.getInstance().reference.child("Documents")
    var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var selectedUris: MutableList<Uri> = mutableListOf() // Add selectedUris property

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(application) as T
        }

        private val _list = MutableLiveData<List<String>>()
        val list: LiveData<List<String>> = _list
    }
}