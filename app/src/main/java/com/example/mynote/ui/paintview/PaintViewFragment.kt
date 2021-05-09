package com.example.mynote.ui.paintview

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynote.databinding.FragmentPaintViewBinding
import com.example.mynote.ui.editdialog.EditDialogFragment
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

private const val TAG = "PaintViewFragment"

@Suppress("DEPRECATION")
class PaintViewFragment : Fragment() {

    // navArgs
    private val args: PaintViewFragmentArgs by navArgs()
    private lateinit var paintView: PaintView

    private var _binding: FragmentPaintViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaintViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paintView = binding.paintView

        // Converting uri to bitmap
        args.bitmapUriString?.let {

            val bitmapUri: Uri = Uri.parse(it)

            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        requireContext().contentResolver,
                        bitmapUri
                    )
                )
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, bitmapUri)
            }
            if (!bitmap.isMutable) {
                val userSelectedMutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                paintView.assignUserSelectedBitmap(userSelectedMutableBitmap)
            } else {
                paintView.assignUserSelectedBitmap(bitmap)
            }
        }


        binding.bottomSheetBtn.setOnClickListener {
            val bottomSheet = EditDialogFragment(paintView)
//            bottomSheet.show(
//                requireActivity().supportFragmentManager,
//                "ModalBottomSheet"
//            )
            bottomSheet.show(childFragmentManager, "ModalBottomSheet")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun discardBitmap() {
        val action =
            PaintViewFragmentDirections.actionPaintViewFragmentToAddEditNoteFragment(
                args.id,
                null
            )
        findNavController().navigate(action)
    }


    fun saveBitmapToInternalStorage() {

        Log.d(TAG, "saveBitmapToInternalStorage: Thread : ${Thread.currentThread().id}")
        lifecycleScope.launch {
            Log.d(
                TAG,
                "saveBitmapToInternalStorage: Inside coroutine Thread : ${Thread.currentThread().id}"
            )
            val dir = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
            )

            if (!dir.exists()) {
                dir.mkdirs()
                Log.d(TAG, "Dir created at  ->  ${dir.absolutePath}")
            }

            val filePath = "$dir/${UUID.randomUUID()}.png"
            val file = File(filePath)

            val output: OutputStream = FileOutputStream(file)

            try {
                paintView.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            } catch (e: IOException) {
                Log.d(TAG, "Exception : $e")
                val action =
                    PaintViewFragmentDirections.actionPaintViewFragmentToAddEditNoteFragment(
                        args.id,
                        null
                    )
                findNavController().navigate(action)
            }
            Log.d(TAG, "download: Image saved in internal storage. ${file.absolutePath}")
            val action = PaintViewFragmentDirections.actionPaintViewFragmentToAddEditNoteFragment(
                args.id,
                file.absolutePath
            )
            findNavController().navigate(action)

        }
    }


}