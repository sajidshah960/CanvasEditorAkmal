package com.example.canvaseditorakmal.canvaseditor.events

import android.view.MotionEvent
import com.example.canvaseditorakmal.canvaseditor.listeners.StickerIconListener
import com.example.canvaseditorakmal.canvaseditor.stickers.StickerView

internal class FlipIconEvent: StickerIconListener {
    override fun onActionDown(stickerView: StickerView?, event: MotionEvent?) {}
    override fun onActionMove(stickerView: StickerView, event: MotionEvent) {}
    override fun onActionUp(stickerView: StickerView, event: MotionEvent?) {
        stickerView.flip()
    }
}