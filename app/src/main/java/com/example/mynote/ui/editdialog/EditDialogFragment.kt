package com.example.mynote.ui.editdialog

import android.app.Dialog
import android.graphics.BlurMaskFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.mynote.R
import com.example.mynote.ui.paintview.PaintView
import com.example.mynote.ui.paintview.PaintViewFragment
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.slider.AlphaSlider
import com.flask.colorpicker.slider.LightnessSlider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ramotion.fluidslider.FluidSlider

class EditDialogFragment(private val paintView: PaintView) : BottomSheetDialogFragment() {

    private lateinit var colorPickerView: ColorPickerView
    private lateinit var vLightnessSlider: LightnessSlider
    private lateinit var vAlphaSlider: AlphaSlider
    private lateinit var fluidSlider: FluidSlider
    private lateinit var blurMenuParentLayout: LinearLayout
    private lateinit var blurChipGroup: ChipGroup
    private lateinit var normalBlurChip: Chip
    private lateinit var innerBlurChip: Chip
    private lateinit var noneBlurChip: Chip
    private lateinit var outerBlurChip: Chip
    private lateinit var solidBlurChip: Chip
    private lateinit var optionsParentLayout1: LinearLayout
    private lateinit var bgChangeBtnChip: Chip
    private lateinit var undoBtnChip: Chip
    private lateinit var eraserBtnChip: Chip
    private lateinit var optionsParentLayout2: LinearLayout
    private lateinit var discardBtnChip: Chip
    private lateinit var clearAllBtnChip: Chip
    private lateinit var saveBtnChip: Chip

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorPickerView = view.findViewById(R.id.color_picker_view)
        fluidSlider = view.findViewById(R.id.fluid_slider)
        vAlphaSlider = view.findViewById(R.id.v_alpha_slider)
        blurMenuParentLayout = view.findViewById(R.id.blur_menu_parent_layout)
        vLightnessSlider = view.findViewById(R.id.v_lightness_slider)
        blurChipGroup = view.findViewById(R.id.blur_chip_group)
        normalBlurChip = view.findViewById(R.id.normal_blur_chip)
        innerBlurChip = view.findViewById(R.id.inner_blur_chip)
        noneBlurChip = view.findViewById(R.id.none_blur_chip)
        outerBlurChip = view.findViewById(R.id.outer_blur_chip)
        solidBlurChip = view.findViewById(R.id.solid_blur_chip)
        optionsParentLayout1 = view.findViewById(R.id.options_parent_layout_1)
        bgChangeBtnChip = view.findViewById(R.id.bg_change_btn_chip)
        undoBtnChip = view.findViewById(R.id.undo_btn_chip)
        eraserBtnChip = view.findViewById(R.id.eraser_btn_chip)
        optionsParentLayout2 = view.findViewById(R.id.options_parent_layout_2)
        discardBtnChip = view.findViewById(R.id.discard_btn_chip)
        clearAllBtnChip = view.findViewById(R.id.clear_all_btn_chip)
        saveBtnChip = view.findViewById(R.id.save_btn_chip)

//----------------------------    Color Part Start   -------------------------------------

        updateColorPickerView()

        colorPickerView.animate()
        colorPickerView.addOnColorSelectedListener {
            Log.d("TAG", "addOnColorSelectedListener called")
        }
        colorPickerView.addOnColorChangedListener {
            Log.d("TAG", "addOnColorChangedListener called")

            updateAllLayoutTheme(it)

            if (paintView.isBgChangeBtnSelected)
                paintView.setBackGroundColor(it)
            else
                paintView.setColour(it)
        }


//----------------------------    Color Part End   -------------------------------------


//----------------------------    Stroke Slider Part Start   -------------------------------------

        // Kotlin
        val min = 1
        val max = 100
        val total = max - min

        val slider = fluidSlider
        slider.position = paintView.mStrokeWidth / 100f
        slider.startText = "$min"
        slider.endText = "$max"

        slider.positionListener = { pos ->
            slider.bubbleText = "${min + (total * pos).toInt()}"
            Log.d("TAG", "${min + (total * pos).toInt()}")

            paintView.setStrokeWidth((min + (total * pos).toInt()).toFloat())
        }

//----------------------------    Stroke Slider Part End   -------------------------------------

//----------------------------    Blur Effect Chip Part Start   -------------------------------------

        updateEventInBlurChipGroup()
        blurChipGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.normal_blur_chip -> {
                    Log.d("TAG", "normalBlurChip clicked ")
                    paintView.setBlurAndBlurEffect(true, BlurMaskFilter.Blur.NORMAL)
                    updateEventInBlurChipGroup()
                }

                R.id.inner_blur_chip -> {
                    Log.d("TAG", "innerBlurChip clicked ")
                    paintView.setBlurAndBlurEffect(true, BlurMaskFilter.Blur.INNER)
                    updateEventInBlurChipGroup()
                }

                R.id.none_blur_chip -> {
                    Log.d("TAG", "noneBlurChip clicked ")
                    paintView.setBlurAndBlurEffect(false, null)
                    updateEventInBlurChipGroup()
                }

                R.id.outer_blur_chip -> {
                    Log.d("TAG", "outerBlurChip clicked ")
                    paintView.setBlurAndBlurEffect(true, BlurMaskFilter.Blur.OUTER)
                    updateEventInBlurChipGroup()
                }

                R.id.solid_blur_chip -> {
                    Log.d("TAG", "solidBlurChip clicked ")
                    paintView.setBlurAndBlurEffect(true, BlurMaskFilter.Blur.SOLID)
                    updateEventInBlurChipGroup()
                }
                else -> {
                    return@setOnCheckedChangeListener
                }
            }
        }

//----------------------------    Blur Effect Chip Part End   -------------------------------------

//----------------------------    Options Part -1 Start   -------------------------------------

        updateEventInOptionsBtn()

        bgChangeBtnChip.setOnCheckedChangeListener { _, _ ->
            paintView.isBgChangeBtnSelected = !paintView.isBgChangeBtnSelected
            updateColorPickerView()
        }

        eraserBtnChip.setOnCheckedChangeListener { _, _ ->
            paintView.isEraserBtnSelected = !paintView.isEraserBtnSelected
            dismiss()
        }

        undoBtnChip.setOnCheckedChangeListener { _, _ ->
            paintView.setUndo()
            dismiss()
        }

//----------------------------    Options Part -1 End   -------------------------------------

//----------------------------    Options Part -2 Start   -------------------------------------

        clearAllBtnChip.setOnCheckedChangeListener { _, _ ->
            paintView.setClearAllPaths()
            dismiss()
        }

        discardBtnChip.setOnCheckedChangeListener { _, _ ->
            (parentFragment as PaintViewFragment).discardBitmap()
            dismiss()
        }

        saveBtnChip.setOnCheckedChangeListener { _, _ ->
            (parentFragment as PaintViewFragment).saveBitmapToInternalStorage()
            dismiss()
        }
//----------------------------    Options Part -2 End   -------------------------------------


    }

    private fun updateAllLayoutTheme(color: Int) {
        fluidSlider.colorBar = color
        fluidSlider.invalidate()

        val bgShapeForBlur: GradientDrawable =
            blurMenuParentLayout.background as GradientDrawable
        val bgShapeForOption1: GradientDrawable =
            optionsParentLayout1.background as GradientDrawable
        val bgShapeForOption2: GradientDrawable =
            optionsParentLayout2.background as GradientDrawable
        bgShapeForBlur.setColor(color)
        bgShapeForOption1.setColor(color)
        bgShapeForOption2.setColor(color)
    }

    private fun updateEventInBlurChipGroup() {
        when (paintView.mBlurEffectName) {
            "NORMAL" -> {
                blurChipGroup.check(R.id.normal_blur_chip)
            }
            "INNER" -> {
                blurChipGroup.check(R.id.inner_blur_chip)
            }
            "NULL" -> {
                blurChipGroup.check(R.id.none_blur_chip)
            }
            "OUTER" -> {
                blurChipGroup.check(R.id.outer_blur_chip)
            }
            "SOLID" -> {
                blurChipGroup.check(R.id.solid_blur_chip)
            }
        }
    }

    private fun updateEventInOptionsBtn() {
        bgChangeBtnChip.isChecked = paintView.isBgChangeBtnSelected
        eraserBtnChip.isChecked = paintView.isEraserBtnSelected
    }

    private fun updateColorPickerView() {
        if (paintView.isBgChangeBtnSelected) {
            colorPickerView.setColor(paintView.mBackgroundColor, false)

            vAlphaSlider.post { vAlphaSlider.setColor(paintView.mBackgroundColor) }
            vLightnessSlider.post { vLightnessSlider.setColor(paintView.mBackgroundColor) }

            updateAllLayoutTheme(colorPickerView.selectedColor)
        } else {
            colorPickerView.setColor(paintView.mColor, false)

            vAlphaSlider.post { vAlphaSlider.setColor(paintView.mColor) }
            vLightnessSlider.post { vLightnessSlider.setColor(paintView.mColor) }

            updateAllLayoutTheme(colorPickerView.selectedColor)
        }
    }
}