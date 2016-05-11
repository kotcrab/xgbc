package com.kotcrab.xgbc.gdx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.kotcrab.xgbc.io.Gpu

/** @author Kotcrab */
class GdxGpu(private val gpu: Gpu) {
    private val tmpTileBuffer = IntArray(64)

    val color0 = Color(224f / 255f, 248f / 255f, 208f / 255f, 1f)
    val color1 = Color(136f / 255f, 192f / 255f, 112f / 255f, 1f)
    val color2 = Color(52f / 255f, 104f / 255f, 86f / 255f, 1f)
    val color3 = Color(8f / 255f, 24f / 255f, 32f / 255f, 1f)
    val colors = arrayOf(Color.rgba8888(color0), Color.rgba8888(color1), Color.rgba8888(color2), Color.rgba8888(color3))

    fun drawPattern1TileToPixmap(pixmap: Pixmap, xOffset: Int, yOffset: Int, tileId: Int) {
        val buffer = gpu.readTilePatternTable1(tileId, tmpTileBuffer);

        for (i in 0..7) {
            for (ii in 0..7) {
                pixmap.drawPixel(xOffset + (7 - ii), yOffset + i, colors[buffer[i * 8 + ii]])
            }
        }
    }
}
