package com.example.fileupload

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class ImagesActivityViewModel(
    private val application: Application
) : ViewModel(){

    class Factory(
        private val application: Application
    ) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            ImagesActivityViewModel(application) as T
    }

    lateinit var firebaseFirestore: FirebaseFirestore
    var mList = mutableListOf<String>()
    lateinit var adapter: ImagesAdapter


    private val _fileList = MutableLiveData<List<String>>()
    val fileList: LiveData<List<String>>
        get() = _fileList

    fun fetchImageFileNames(callback: (List<String>) -> Unit) {
        val fileNames = mutableListOf<String>()
        firebaseFirestore.collection("images")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val fileName = document.getString("fileName")
                    fileName?.let {
                        fileNames.add(it)
                    }
                }
                callback(fileNames)
            }
            .addOnFailureListener { exception ->
                // Handle failure
                callback(emptyList())
            }
    }
}
