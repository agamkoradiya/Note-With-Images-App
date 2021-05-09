package com.example.mynote.ui.addeditnote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynote.R
import com.example.mynote.data.local.entities.Note
import com.example.mynote.databinding.FragmentAddEditNoteBinding
import com.example.mynote.ui.addeditnote.adapter.ImagePreviewAdapter
import com.example.mynote.util.OnItemClickListener
import com.example.mynote.util.toast
import com.example.mynote.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*

private const val TAG = "AddEditNoteFragment"
private lateinit var photoFile: File

@AndroidEntryPoint
class AddEditNoteFragment : Fragment(), OnItemClickListener {

    // navArgs
    private val args: AddEditNoteFragmentArgs by navArgs()

    // View binding variable
    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    // ViewModel Injection
    private val addEditNoteViewModel: AddEditNoteViewModel by viewModels()
    private val viewModel: ViewModel by viewModels()

    // For camera/gallery
    private val photoFileName = UUID.randomUUID().toString()

    // Global variables for Note
    private lateinit var mId: String
    private var mImageUrls: MutableList<String> = mutableListOf()

    // Adapter initialization
    private lateinit var adapter: ImagePreviewAdapter

    // New way of start activity for result : Camera result
    private val startActivityForCameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "takePhoto: ${photoFile.toUri()}")
                val action =
                    AddEditNoteFragmentDirections.actionAddEditNoteFragmentToPaintViewFragment(
                        photoFile.toUri().toString(),
                        mId
                    )
                findNavController().navigate(action)
            }
        }

    // New way of start activity for result : Gallery result
    private val startActivityForGalleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "takePhoto: ${result.data?.data}")
                val action =
                    AddEditNoteFragmentDirections.actionAddEditNoteFragmentToPaintViewFragment(
                        result.data?.data.toString(),
                        mId
                    )
                findNavController().navigate(action)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Getting data [id, saved bitmap path] via safe args
        Log.d(TAG, "onViewCreated: id = ${args.id}")
        Log.d(TAG, "onViewCreated: savedBitmapPath = ${args.savedBitmapPath}")


        // Set image preview adapter in recyclerview
        adapter = ImagePreviewAdapter(this)
        binding.imagePreviewRecyclerView.adapter = adapter
//        Log.d(TAG, "before submit list -> ${mImageUrls.toString()}")
//        adapter.submitList(mImageUrls)

        // Checking args id is null or not
        if (args.id.isNullOrEmpty()) {
            mId = UUID.randomUUID().toString()
        } else {
            addEditNoteViewModel.getNoteById(args.id!!)
            addEditNoteViewModel.savedNote.observe(
                viewLifecycleOwner,
                { savedNote ->
                    mId = savedNote.id
                    binding.titleEt.editText?.setText(savedNote.title)
                    binding.prioritiesSpinner.setSelection(
                        viewModel.getIndexFromPriority(
                            savedNote.priority
                        )
                    )
                    binding.contentEt.editText?.setText(savedNote.content)
                    savedNote.imageUrls?.let {
                        mImageUrls.addAll(it)
                        Log.d(TAG, "before - ${it.size}")
                        Log.d(TAG, "insider observer -> $mImageUrls")
                    }
                    // Add recently saved image in mImageUrls variable
                    args.savedBitmapPath?.let { mImageUrls.add(it) }
                    Log.d(TAG, "before submit list -> $mImageUrls")
                    Log.d(TAG, "after -> ${mImageUrls.size}")
                    adapter.submitList(mImageUrls)
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_edit_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.take_photo -> {
                if (verifyTitleValue()) {
                    takePhoto()
                } else {
                    requireContext().toast("Title required")
                }
                true
            }
            R.id.add_image -> {
                if (verifyTitleValue()) {
                    addImage()
                } else {
                    requireContext().toast("Title required")
                }
                true
            }
            R.id.drawing -> {
                if (verifyTitleValue()) {
                    hideSoftKeyboard(requireActivity())
                    drawing()
                } else {
                    requireContext().toast("Title required")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun drawing() {
        val action =
            AddEditNoteFragmentDirections.actionAddEditNoteFragmentToPaintViewFragment(
                null,
                mId
            )
        findNavController().navigate(action)
    }

    private fun addImage() {
        // Create intent for picking a photo from the gallery
        val addImageIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Checking gallery in your phone
        if (addImageIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForGalleryResult.launch(addImageIntent)
        } else {
            requireContext().toast("Gallery not found")
        }
    }

    private fun takePhoto() {
        // Create intent for opening camera
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(photoFileName)

        val fileProvider =
            FileProvider.getUriForFile(
                requireContext(),
                "com.example.mynote.fileprovider",
                photoFile
            )
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        // Checking camera in your phone
        if (takePhotoIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForCameraResult.launch(takePhotoIntent)
        } else {
            requireContext().toast("CAMERA not found")
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    private fun saveNote() {

        val mTitle = binding.titleEt.editText?.text.toString().trim()
        val mPriority =
            viewModel.parsePriority(binding.prioritiesSpinner.selectedItem.toString())
        val mContent = binding.contentEt.editText?.text.toString().trim()
        val mDate = System.currentTimeMillis()
//        args.savedBitmapPath?.let { mImageUrls.add(it) }

        val validation = verifyTitleValue()
        if (validation) {
            val note = Note(
                mId,
                mDate,
                mTitle,
                mPriority,
                mContent,
                mImageUrls
            )
            if (args.id.isNullOrEmpty()) {
                addEditNoteViewModel.insertNote(note)
                Log.d(TAG, "Note saved successfully ")
            } else {
                addEditNoteViewModel.updateNote(note)
                Log.d(TAG, "Note updated successfully ")
            }
        } else {
            requireContext().toast("Note discard")
        }
    }

    private fun verifyTitleValue(): Boolean {
        return viewModel.verifyDataFromUser(binding.titleEt.editText?.text.toString().trim())
    }

    override fun onPause() {
        saveNote()
        Log.d(TAG, "onPause: ")
        Log.d(
            TAG,
            "onPause: -----------------------------------------------------------------------"
        )
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {

        Log.d(TAG, "onItemClick: delete btn pressed image position -> $position")

        val fileDelete: File = File(mImageUrls[position])
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                requireContext().toast("Image deleted")

                mImageUrls.removeAt(position)
                adapter.submitList(mImageUrls)
                adapter.notifyDataSetChanged()

            } else {
                requireContext().toast("Image can't delete")
            }
        }
    }

    override fun onItemClick(idOrPath: String) {
        TODO("Not yet implemented")
    }

    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                0
            )
        }
    }
}