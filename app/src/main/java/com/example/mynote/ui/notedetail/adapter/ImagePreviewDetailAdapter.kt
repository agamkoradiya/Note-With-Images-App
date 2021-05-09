package com.example.mynote.ui.notedetail.adapter

import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mynote.databinding.ItemImageDetailPreviewBinding
import com.example.mynote.util.OnItemClickListener
import com.example.mynote.util.StringDiffCallback


private const val TAG = "ImagePreviewDetailAdapter"

class ImagePreviewDetailAdapter(private val listener: OnItemClickListener) :
    ListAdapter<String, ImagePreviewDetailAdapter.ImagePreviewViewHolder>(StringDiffCallback()) {

    inner class ImagePreviewViewHolder(private val binding: ItemImageDetailPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imagePreview.setOnClickListener {
                val position = layoutPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position))
                }
            }
        }

        // set image in preview img view
        fun bind(path: String) {
//            binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(path));
            val thumbImage = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(path),
                90,
                130
            )
            binding.imagePreview.setImageBitmap(thumbImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePreviewViewHolder {
        val binding =
            ItemImageDetailPreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ImagePreviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagePreviewViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}