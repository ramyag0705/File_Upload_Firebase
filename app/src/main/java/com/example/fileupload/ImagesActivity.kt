package com.example.fileupload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fileupload.databinding.ActivityImagesBinding
import com.google.firebase.firestore.FirebaseFirestore

class ImagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagesBinding

    private val viewModel: ImagesActivityViewModel by viewModels {
        ImagesActivityViewModel.Factory(
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        getImages()
    }

    private fun initVars() {
        viewModel.firebaseFirestore = FirebaseFirestore.getInstance()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.adapter = ImagesAdapter(viewModel.mList)
        binding.recyclerView.adapter = viewModel.adapter
    }

    private fun getImages() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchImageFileNames { fileNames ->
            val adapter = binding.recyclerView.adapter as? ImagesAdapter
            adapter?.setData(fileNames)
            binding.progressBar.visibility = View.GONE
        }
    }
}


