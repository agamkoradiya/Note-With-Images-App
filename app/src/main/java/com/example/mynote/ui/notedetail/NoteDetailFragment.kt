package com.example.mynote.ui.notedetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynote.databinding.FragmentNoteDetailBinding
import com.example.mynote.ui.notedetail.adapter.ImagePreviewDetailAdapter
import com.example.mynote.util.OnItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

@AndroidEntryPoint
class NoteDetailFragment : Fragment(), OnItemClickListener {

    // View binding variable
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

    // navArgs
    private val args: NoteDetailFragmentArgs by navArgs()

    // ViewModel Injection
    private val noteDetailViewModel: NoteDetailViewModel by viewModels()

    // Adapter initialization
    private lateinit var adapter: ImagePreviewDetailAdapter

    private var id: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set image preview adapter in recyclerview
        adapter = ImagePreviewDetailAdapter(this)
        binding.imagePreviewRecyclerView.adapter = adapter

        // Observing note object from the id by view model
        noteDetailViewModel.observeNoteByID(args.id).observe(viewLifecycleOwner, { note ->
            this.id = note.id
            // Setting note data to the view
            binding.titleTxt.text = note.title
            note.content?.let { setMarkdownText(it) }
            adapter.submitList(note.imageUrls)
        })

        binding.editNoteFab.setOnClickListener {
            val action =
                NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(id, null)
            findNavController().navigate(action)
        }
    }


    private fun setMarkdownText(text: String) {
        val markwon = Markwon.create(requireContext())
        val markdown = markwon.toMarkdown(text)
        markwon.setParsedMarkdown(binding.contentTxt, markdown)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(idOrPath: String) {
        TODO("Not yet implemented")
    }
}