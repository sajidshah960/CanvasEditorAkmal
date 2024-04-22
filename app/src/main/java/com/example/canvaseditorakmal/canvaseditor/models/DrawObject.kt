package com.example.canvaseditorakmal.canvaseditor.models

import com.example.canvaseditorakmal.canvaseditor.enums.DrawType
import com.example.canvaseditorakmal.canvaseditor.stickers.Sticker

internal data class DrawObject(
    val pathAndPaint: PathAndPaint?,
    val sticker: Sticker?,
    val drawType: DrawType
)