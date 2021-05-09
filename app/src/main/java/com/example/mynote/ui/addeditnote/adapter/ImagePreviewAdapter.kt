package com.example.mynote.ui.addeditnote.adapter

import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mynote.databinding.ItemImagePreviewBinding
import com.example.mynote.util.OnItemClickListener
import com.example.mynote.util.StringDiffCallback


private const val TAG = "ImagePreviewAdapterAdapter"

class ImagePreviewAdapter(private val listener: OnItemClickListener) :
    ListAdapter<String, ImagePreviewAdapter.ImagePreviewViewHolder>(StringDiffCallback()) {

    inner class ImagePreviewViewHolder(private val binding: ItemImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.deleteImageBtn.setOnClickListener {
                val position = layoutPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }

        // set image in preview img view
        fun bind(path: String) {
            // For high quality
//            binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(path));

            // For thumbnail
            val thumbImage = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(path),
                70,
                110
            )
            binding.imagePreview.setImageBitmap(thumbImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePreviewViewHolder {
        val binding =
            ItemImagePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImagePreviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagePreviewViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}

