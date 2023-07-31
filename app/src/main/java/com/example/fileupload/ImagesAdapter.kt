package com.example.fileupload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fileupload.databinding.EachItemBinding

class ImagesAdapter(private var mList: List<String>) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    inner class ImagesViewHolder(var binding: EachItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val fileName = mList[position]
        holder.binding.fileNameTextView.text = fileName
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setData(data: List<String>) {
        mList = data
        notifyDataSetChanged()
    }
}