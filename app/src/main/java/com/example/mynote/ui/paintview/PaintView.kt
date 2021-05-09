package com.example.mynote.ui.paintview


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.mynote.data.FingerData
import java.util.*
import kotlin.math.abs

private const val TOUCH_TOLERANCE = 5f
private const val TAG = "FinalPaintView"
private const val DEFAULT_BRUSH_SIZE: Float = 10f
private const val DEFAULT_COLOR: Int = Color.BLACK
private const val DEFAULT_BG_COLOR: Int = Color.LTGRAY


class PaintView : View {

    // Base variable for PaintView
    private var _mBitmap: Bitmap? = null
    val mBitmap: Bitmap
        get() = _mBitmap!!
    private var _mCanvas: Canvas? = null
    private val mCanvas: Canvas
        get() = _mCanvas!!

    //    private var mBitmapPaint: Paint? = null
    private var mSrcRectF: RectF = RectF()
    private var mDestRectF: RectF = RectF()
    private var mMatrix: Matrix = Matrix()


    // User selected bitmap Variable
    private var userSelectedBitmap: Bitmap? = null

    // Variable for editing
    private var mPaint: Paint = Paint()
    private lateinit var mPath: Path

    private var mX = 0f
    private var mY = 0f

    private val drawnPathDataList: MutableList<FingerData> = mutableListOf()
    private val deletedDrawnPathDataList: MutableList<FingerData> = mutableListOf()
    var mColor: Int = DEFAULT_COLOR
    var mBackgroundColor: Int = DEFAULT_BG_COLOR
    var mStrokeWidth: Float = DEFAULT_BRUSH_SIZE
    private var mIsBlur: Boolean = false
    private var mBlurEffect: MaskFilter? = null
    var mBlurEffectName: String = "NULL"

    var isBgChangeBtnSelected: Boolean = false
    var isEraserBtnSelected: Boolean = false


    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        Log.d(TAG, ": Second Constructor Called")

        // Initializing default value of paint
        mPaint.color = mColor
        mPaint.strokeWidth = mStrokeWidth
        mPaint.maskFilter = mBlurEffect

        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isDither = true

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        _mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        _mCanvas = Canvas(mBitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvas?.save()
        mCanvas.drawColor(mBackgroundColor)

        userSelectedBitmap?.let {
            // Setting size of Source Rect
            mSrcRectF.set(
                0f,
                0f,
                it.width.toFloat(),
                it.height.toFloat()
            )
            // Setting size of Destination Rect
            mDestRectF.set(10f, 10f, (width.toFloat() - 10f), (height.toFloat() - 10f))
            // Scaling the bitmap to fit the PaintView
            mMatrix.setRectToRect(mSrcRectF, mDestRectF, Matrix.ScaleToFit.CENTER)
            // Drawing userSelected image on canvas
            mCanvas.drawBitmap(it, mMatrix, mPaint)
        }

        for (fingerData in drawnPathDataList) {
            mPaint.color = fingerData.color
            mPaint.strokeWidth = fingerData.strokeWidth
            mPaint.maskFilter = null
            if (fingerData.isBlur) {
                mPaint.maskFilter = fingerData.blurEffect
            }
            mCanvas.drawPath(fingerData.path, mPaint)
        }

        canvas?.drawBitmap(mBitmap, 0f, 0f, mPaint)
//        canvas?.restore()
    }

    fun setColour(color: Int) {
        mColor = color
    }

    fun setStrokeWidth(strokeWidth: Float) {
        mStrokeWidth = strokeWidth
    }

    fun setBlurAndBlurEffect(isBlur: Boolean, blurEffect: BlurMaskFilter.Blur?) {
        mBlurEffectName = blurEffect.toString().toUpperCase(Locale.ROOT)
        mIsBlur = isBlur
        mBlurEffect = if (isBlur)
            BlurMaskFilter(mStrokeWidth, blurEffect)
        else
            null
        invalidate()
    }

    fun setBackGroundColor(color: Int) {
        mBackgroundColor = color
        invalidate()
    }

    fun setUndo() {
        if (isEraserBtnSelected) {
            val size = deletedDrawnPathDataList.size
            if (size >= 1) {
                drawnPathDataList.add(deletedDrawnPathDataList[size - 1])
                deletedDrawnPathDataList.removeAt(size - 1)
                invalidate()
            }
        } else {
            val size = drawnPathDataList.size
            if (size >= 1) {
                drawnPathDataList.removeAt(size - 1)
                invalidate()
            }
        }
    }

    fun setClearAllPaths() {
        drawnPathDataList.clear()
        deletedDrawnPathDataList.clear()
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y

        if (x != null && y != null) {

            if (isEraserBtnSelected) {

                when (event.action) {

                    MotionEvent.ACTION_MOVE -> {

                        for (fd in drawnPathDataList) {
                            val pBounds = RectF()
                            fd.path.computeBounds(pBounds, true)

                            if (pBounds.bottom == pBounds.top) {
                                Log.d(TAG, "Added 5 points ")

                                pBounds.right += 5f
                                pBounds.left -= 5f
                                pBounds.bottom += 5f
                                pBounds.top -= 5f
                            }
                            Log.d(TAG, "left  ->  ${pBounds.left} ")
                            Log.d(TAG, "right  ->  ${pBounds.right} ")
                            Log.d(TAG, "bottom  ->  ${pBounds.bottom} ")
                            Log.d(TAG, "top  ->  ${pBounds.top} ")
                            Log.d(TAG, "x  -> $x ")
                            Log.d(TAG, "y  -> $y ")
                            Log.d(TAG, "------------------------------------")

                            if (pBounds.contains(x, y)) {
                                deletedDrawnPathDataList.add(fd)
                                drawnPathDataList.remove(fd)
                                invalidate()
                                break
                            }

                        }
                    }
                }
            } else {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touchStart(x, y)
                        invalidate()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        touchMove(x, y)
                        invalidate()
                    }
                    MotionEvent.ACTION_UP -> {
                        touchUp()
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fingerData = FingerData(mColor, mIsBlur, mBlurEffect, mStrokeWidth, mPath)
        drawnPathDataList.add(fingerData)
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        mPath.lineTo(mX, mY)
        // kill this so we don't double draw
//        mPath.reset()
    }

    fun assignUserSelectedBitmap(_userSelectedBitmap: Bitmap) {
        userSelectedBitmap = _userSelectedBitmap
        invalidate()
    }
}