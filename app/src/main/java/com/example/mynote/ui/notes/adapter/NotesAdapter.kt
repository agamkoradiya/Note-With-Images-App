package com.example.mynote.ui.notes.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mynote.data.local.entities.Note
import com.example.mynote.data.local.entities.Priority
import com.example.mynote.databinding.ItemNoteBinding
import com.example.mynote.util.OnItemClickListener
import java.text.SimpleDateFormat
import java.util.*


class NotesAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Note, NotesAdapter.NotesAdapterViewHolder>(NoteDiffCallback()) {

    inner class NotesAdapterViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.noteParentLayout.setOnClickListener {
                val position = layoutPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { it1 -> listener.onItemClick(it1.id) }
                }
            }
        }

        fun bind(note: Note) {
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            val dateString = dateFormat.format(note.date)
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val timeString = timeFormat.format(note.date)

            binding.apply {
                dateTxt.text = dateString
                timeTxt.text = timeString
                titleTxt.text = note.title

                when (note.priority) {
                    Priority.HIGH -> priorityColorCardView.setCardBackgroundColor(Color.RED)
                    Priority.MEDIUM -> priorityColorCardView.setCardBackgroundColor(Color.BLUE)
                    Priority.LOW -> priorityColorCardView.setCardBackgroundColor(Color.GREEN)
                }
                if (!note.imageUrls.isNullOrEmpty()) {
                    binding.isImagesIncludedImg.visibility = View.VISIBLE
//                    when (note.priority) {
//                        Priority.HIGH -> isImagesIncludedImg.setImageResource(R.drawable.ic_red_image)
//                        Priority.MEDIUM -> isImagesIncludedImg.setImageResource(R.drawable.ic_blue_image)
//                        Priority.LOW -> isImagesIncludedImg.setImageResource(R.drawable.ic_green_image)
//                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: NotesAdapterViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesAdapterViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesAdapterViewHolder(binding)
    }
}

class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
}
