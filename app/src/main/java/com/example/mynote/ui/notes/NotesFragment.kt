package com.example.mynote.ui.notes

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mynote.R
import com.example.mynote.ui.notes.adapter.NotesAdapter
import com.example.mynote.databinding.FragmentNotesBinding
import com.example.mynote.util.OnItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "NotesFragment"

@AndroidEntryPoint
class NotesFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    // Initializing adapter
    private lateinit var adapter: NotesAdapter

    // View model Inject
    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting option menu
        setHasOptionsMenu(true)

        // Adapter work
        setupRecyclerview()


        binding.addNewNoteFab.setOnClickListener {
            val action =
                NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(null, null)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerview() {
        val recyclerView = binding.notesRecyclerView
        adapter = NotesAdapter(this)
        recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            notesViewModel.getAllNotes().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.menu_priority_high -> {

                true
            }
            R.id.menu_priority_low -> {

                true
            }
            R.id.menu_delete_all -> {

                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(idOrPath: String) {
        Log.d("TAG", "onItemClickId: $idOrPath")
        val action = NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(idOrPath)
        findNavController().navigate(action)
    }

}