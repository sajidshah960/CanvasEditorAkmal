package com.example.canvaseditorakmal.canvaseditor.listeners

import android.view.MotionEvent
import com.example.canvaseditorakmal.canvaseditor.models.DrawObject

internal interface PaintViewListener {
    fun onTouchUp(obj: DrawObject)
    fun onClick(x: Float, y: Float)
    fun onTouchEvent(event: MotionEvent)
}