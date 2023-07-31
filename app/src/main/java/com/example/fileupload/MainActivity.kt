package com.example.fileupload

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.fileupload.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var imageArray = ArrayList<String>()

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        registerClickEvents()
    }

    private fun initVars() {
        viewModel.storageRef
        viewModel.firebaseFirestore
    }

    private fun registerClickEvents() {
        binding.apply {
            uploadBtn.setOnClickListener {
                uploadImage()
            }
            imageView.setOnClickListener {
                resultLauncher.launch(arrayOf("*/*")) // Allow selecting any file type
            }
        }
        binding.showAllBtn.setOnClickListener {
            startActivity(Intent(this, ImagesActivity::class.java))
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        handleSelectedFiles(uris)
    }

    private fun handleSelectedFiles(uris: List<Uri>?) {
        uris?.let {
            imageArray.clear()
            viewModel.selectedUris.clear() // Clear the selected URIs
            for (uri in it) {
                val mimeType = contentResolver.getType(uri)
                if (mimeType == "application/pdf") {
                    val selectedFileName = getFileNameFromUri(uri)
                    selectedFileName?.let {
                        imageArray.add(it)
                        viewModel.selectedUris.add(uri) // Add the URI to selectedUris list
                    }
                } else if (mimeType?.startsWith("image/") == true) {
                    // Handle the selected image file
                    // You can add the necessary code here to process the image file
                    binding.imageView.setImageURI(uri)
                    viewModel.selectedUris.add(uri) // Add the URI to selectedUris list
                }
            }
            binding.selectedFileNameEditText.setText(imageArray.joinToString(", "))
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    it.getString(displayNameIndex)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    private fun uploadImage() {
        binding.progressBar.visibility = View.VISIBLE

        for (uri in viewModel.selectedUris) {
            val fileName = getFileNameFromUri(uri) ?: ""
            val storageRef = viewModel.storageRef.child(fileName)
            val mimeType = contentResolver.getType(uri)

            if (mimeType == "application/pdf") {
                // Handle PDF file upload
                storageRef.putFile(uri).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageRef.downloadUrl.addOnSuccessListener { pdfUri ->
                            val map = HashMap<String, Any>()
                            map["pdf"] = pdfUri.toString()
                            map["fileName"] = fileName

                            viewModel.firebaseFirestore.collection("images").add(map)
                                .addOnCompleteListener { firestoreTask ->
                                    if (firestoreTask.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "PDF Uploaded Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            firestoreTask.exception?.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    binding.progressBar.visibility = View.GONE
                                    binding.imageView.setImageResource(R.drawable.vector)
                                    binding.selectedFileNameEditText.setText("") // Clear the selected file name
                                }
                        }
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.imageView.setImageResource(R.drawable.vector)
                        binding.selectedFileNameEditText.setText("") // Clear the selected file name
                    }
                }
            } else if (mimeType?.startsWith("image/") == true) {
                // Handle image file upload
                storageRef.putFile(uri).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageRef.downloadUrl.addOnSuccessListener { imageUri ->
                            val map = HashMap<String, Any>()
                            map["image"] = imageUri.toString()
                            map["fileName"] = fileName

                            viewModel.firebaseFirestore.collection("images").add(map)
                                .addOnCompleteListener { firestoreTask ->
                                    if (firestoreTask.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Image Uploaded Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            firestoreTask.exception?.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    binding.progressBar.visibility = View.GONE
                                    binding.imageView.setImageResource(R.drawable.vector)
                                    binding.selectedFileNameEditText.setText("") // Clear the selected file name
                                }
                        }
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.imageView.setImageResource(R.drawable.vector)
                        binding.selectedFileNameEditText.setText("") // Clear the selected file name
                    }
                }
            }
        }
    }
}