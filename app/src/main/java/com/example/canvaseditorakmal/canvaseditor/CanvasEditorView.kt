package com.example.canvaseditorakmal.canvaseditor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.example.canvaseditorakmal.canvaseditor.enums.DrawType
import com.example.canvaseditorakmal.canvaseditor.listeners.CanvasEditorListener
import com.example.canvaseditorakmal.canvaseditor.listeners.PaintViewListener
import com.example.canvaseditorakmal.canvaseditor.listeners.StickerViewListener
import com.example.canvaseditorakmal.canvaseditor.models.DrawObject
import com.example.canvaseditorakmal.canvaseditor.paints.PaintView
import com.example.canvaseditorakmal.canvaseditor.stickers.BitmapSticker
import com.example.canvaseditorakmal.canvaseditor.stickers.DrawableSticker
import com.example.canvaseditorakmal.canvaseditor.stickers.StickerView
import com.example.canvaseditorakmal.canvaseditor.stickers.TextSticker

class CanvasEditorView : RelativeLayout{
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private val mUndoList = mutableListOf<DrawObject>()
    private val mRedoList = mutableListOf<DrawObject>()
    private var mImgSource: ImageView = ImageView(context)

    private val paintViewListener = object : PaintViewListener {
        override fun onTouchUp(obj: DrawObject) {
            mUndoList.add(obj)
            mRedoList.clear()
            mListener?.onEnableUndo(true)
            mListener?.onEnableRedo(false)
        }

        override fun onClick(x: Float, y: Float) {
            val pos = findTapedSticker(x, y)
            if(pos > -1)
                enableEditModeSticker(pos)
        }

        override fun onTouchEvent(event: MotionEvent) {
            mListener?.onTouchEvent(event)
        }
    }

    private val stickerViewListener = object : StickerViewListener {
        override fun onRemove() {
            mListener?.onStickerRemove()
            mListener?.onEnableUndo(mUndoList.isNotEmpty())
        }
        override fun onDone(obj: DrawObject) {
            addStickerToPaint(obj)
            mListener?.onStickerDone()
        }
        override fun onZoomAndRotate() {
            mListener?.onStickerZoomAndRotate()
        }
        override fun onFlip() {
            mListener?.onStickerFlip()
        }

        override fun onClickStickerOutside(x: Float, y: Float) {
            val pos = findTapedSticker(x, y)
            if(pos > -1){
                enableEditModeSticker(pos)
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            mListener?.onTouchEvent(event)
        }
    }

    private val mPaintView: PaintView = PaintView(context, paintViewListener)
    private val mStickerView: StickerView = StickerView(context, stickerViewListener)
    private var mListener: CanvasEditorListener? = null

    init {
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        mPaintView.layoutParams = params
        mPaintView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))

        addView(mPaintView)

        mStickerView.layoutParams = params
        mStickerView.setBackgroundColor(
            ContextCompat.getColor(
                context,
                android.R.color.transparent
            )
        )
        addView(mStickerView)
        mStickerView.visibility = View.GONE
    }

    fun setListener(listener: CanvasEditorListener) {
        mListener = listener
    }

    fun setPaintColor(color: Int) {
        doneStickerEdit()
        mPaintView.paint.color = color
    }

    fun setStrokeWidth(strokeWidth: Float) {
        doneStickerEdit()
        mPaintView.paint.strokeWidth = strokeWidth
    }

    fun setEraserEnabled(isEraser: Boolean)
    {
        doneStickerEdit()
        if (isEraser)
        {
            mPaintView.paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))
            mPaintView.paint.strokeWidth = 25f
        }
        else{
            mPaintView.paint.strokeWidth = 10f
            mPaintView.paint.setXfermode(null)
        }
    }

    fun setStrokeCap(strokeCap: Paint.Cap) {
        doneStickerEdit()
        mPaintView.paint.strokeCap = strokeCap
    }

    //region add sticker
    fun addDrawableSticker(drawable: Drawable) {
        doneStickerEdit()
        mStickerView.visibility = View.VISIBLE
        val sticker = DrawableSticker(drawable)
        mStickerView.addSticker(sticker)
        mListener?.onEnableUndo(true)
        mListener?.onEnableRedo(false)
        mListener?.onStickerActive()
    }

    fun addBitmapSticker(bitmap: Bitmap) {
        doneStickerEdit()
        mStickerView.visibility = View.VISIBLE
        val sticker = BitmapSticker(context, bitmap)
        mStickerView.addSticker(sticker)
        mListener?.onEnableUndo(true)
        mListener?.onEnableRedo(false)
        mListener?.onStickerActive()
    }

    fun addTextSticker(text: String, textColor: Int, typeface: Typeface?) {
        doneStickerEdit()
        mStickerView.visibility = View.VISIBLE
        val sticker = TextSticker(context, null)
        sticker.setText(text)
        sticker.setTextColor(textColor)
        typeface?.let {
            sticker.setTypeface(it)
        }
        sticker.setAlpha(255)
        sticker.resizeText()
        mStickerView.addSticker(sticker)
        mListener?.onEnableUndo(true)
        mListener?.onEnableRedo(false)
        mListener?.onStickerActive()
    }

    fun addDrawableTextSticker(
        drawable: Drawable,
        text: String,
        textColor: Int,
        typeface: Typeface?
    ) {
        doneStickerEdit()
        mStickerView.visibility = View.VISIBLE
        val sticker = TextSticker(context, drawable)
        sticker.setText(text)
        sticker.setTextColor(textColor)
        typeface?.let {
            sticker.setTypeface(it)
        }
        sticker.resizeText()
        mStickerView.addSticker(sticker)
        mListener?.onEnableUndo(true)
        mListener?.onEnableRedo(false)
        mListener?.onStickerActive()
    }

    fun doneActiveSticker(){
        if (mStickerView.visibility == View.VISIBLE) {
            mStickerView.done()
        }
    }

    fun removeActiveSticker(){
        if (mStickerView.visibility == View.VISIBLE) {
            mStickerView.remove()
        }
    }

    fun zoomAndRotateActiveSticker(motionEvent: MotionEvent){
        if (mStickerView.visibility == View.VISIBLE) {
            mStickerView.zoomAndRotate(motionEvent)
        }
    }

    fun flipActiveSticker(){
        if (mStickerView.visibility == View.VISIBLE) {
            mStickerView.flip()
        }
    }
    //endregion

    fun undo() {
        if (mStickerView.visibility == View.VISIBLE) {
            mStickerView.remove()
            return
        }
        if (mUndoList.isNotEmpty()) {
            mRedoList.add(mUndoList.last())
            mUndoList.removeAt(mUndoList.lastIndex)
            mPaintView.initCanvas()
            mUndoList.forEach {
                drawObject(it)
            }
            mListener?.onEnableUndo(mUndoList.isNotEmpty())
            mListener?.onEnableRedo(mRedoList.isNotEmpty())
        }
    }

    fun redo() {
        if (mRedoList.isNotEmpty()) {
            val obj = mRedoList.last()
            mUndoList.add(obj)
            mRedoList.removeAt(mRedoList.lastIndex)
            drawObject(obj)
            mListener?.onEnableUndo(mUndoList.isNotEmpty())
            mListener?.onEnableRedo(mRedoList.isNotEmpty())
        }
    }

    fun removeAll(){
        mUndoList.clear()
        mRedoList.clear()
        mStickerView.remove()
        mPaintView.initCanvas()
        mListener?.onEnableUndo(false)
        mListener?.onEnableRedo(false)
    }

    fun downloadBitmap(): Bitmap{
        doneStickerEdit()
        return mPaintView.extraBitmap
    }

    private fun drawObject(obj: DrawObject) {
        when (obj.drawType) {
            DrawType.PATH -> {
                mPaintView.drawPath(obj.pathAndPaint!!)
            }
            DrawType.STICKER -> {
                mPaintView.drawSticker(obj.sticker!!)
            }
        }
    }

    //region find double tap inside sticker
    private fun findTapedSticker(x: Float, y: Float): Int {
        for (i in mUndoList.size - 1 downTo 0) {
            val obj = mUndoList[i]
            if (obj.drawType == DrawType.STICKER) {
                val sticker = obj.sticker!!
                if (sticker.contains(x, y)) {
                    return i
                }
            }
        }
        return -1
    }

    private fun enableEditModeSticker(pos: Int) {
        val obj = mUndoList[pos]
        val sticker = obj.sticker!!
        mStickerView.visibility = View.VISIBLE
        mStickerView.currentSticker = sticker
        mUndoList.removeAt(pos)
        mPaintView.initCanvas()
        mUndoList.forEach {
            drawObject(it)
        }
        mRedoList.clear()
        mListener?.onEnableUndo(true)
        mListener?.onEnableRedo(mRedoList.isNotEmpty())
        mListener?.onStickerActive()
    }

    private fun addStickerToPaint(obj: DrawObject) {
        mPaintView.drawSticker(obj.sticker!!)
        mUndoList.add(obj)
        mRedoList.clear()
        mListener?.onEnableUndo(true)
        mListener?.onEnableRedo(false)
    }

    private fun doneStickerEdit() {
        if (mStickerView.visibility == View.VISIBLE) {
            mStickerView.done()
        }
    }
}
//endregion