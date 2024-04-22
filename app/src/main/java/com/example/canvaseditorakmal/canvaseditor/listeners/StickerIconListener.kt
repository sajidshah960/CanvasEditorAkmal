package com.example.canvaseditorakmal.canvaseditor.listeners

import android.view.MotionEvent
import com.example.canvaseditorakmal.canvaseditor.stickers.StickerView

internal interface StickerIconListener {
    fun onActionDown(stickerView: StickerView?, event: MotionEvent?)
    fun onActionMove(stickerView: StickerView, event: MotionEvent)
    fun onActionUp(stickerView: StickerView, event: MotionEvent?)
}