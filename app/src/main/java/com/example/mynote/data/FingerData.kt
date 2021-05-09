package com.example.mynote.data

import android.graphics.MaskFilter
import android.graphics.Path

data class FingerData(
    var color: Int,
    var isBlur: Boolean,
    var blurEffect: MaskFilter? = null,
    var strokeWidth: Float,
    var path: Path
)
