package com.kotcrab.xgbc.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Timer
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.toInt

/** @author Kotcrab */

class VRAMWindow(val emulator: Emulator) : VisWindow("VRAM") {
    private companion object {
        val TILE_SIZE = 8
        val PALETTE_WIDTH = 16
        val PALETTE_HEIGHT = 16
    }

    val color0 = Color(224f / 255f, 248f / 255f, 208f / 255f, 1f)
    val color1 = Color(136f / 255f, 192f / 255f, 112f / 255f, 1f)
    val color2 = Color(52f / 255f, 104f / 255f, 86f / 255f, 1f)
    val color3 = Color(8f / 255f, 24f / 255f, 32f / 255f, 1f)
    val colors = arrayOf(Color.rgba8888(color0), Color.rgba8888(color1), Color.rgba8888(color2), Color.rgba8888(color3))

    private val pixmap = Pixmap(PALETTE_WIDTH * TILE_SIZE, PALETTE_HEIGHT * TILE_SIZE, Pixmap.Format.RGB888)
    private val texture = Texture(pixmap)

    init {
        val img = VisImage(TextureRegionDrawable(TextureRegion(texture)));
        add(img).size(256f, 256f)
        pack()

        Timer.schedule(object : Timer.Task() {
            override fun run() {
                var lineIdx = 0
                var tileX = 0
                var tileY = 0
                for (i in 0x8000..0x8FFF step 2) {
                    val byte = emulator.readInt(i)
                    val byte2 = emulator.readInt(i + 1)

                    for (ii in 0..7) {
                        val colorLSB = byte and (1 shl ii) != 0
                        val colorMSB = ((byte2 and (1 shl ii)) shl 1) != 0
                        val colorVal = colorLSB.toInt() or colorMSB.toInt()
                        pixmap.drawPixel(tileX * TILE_SIZE + (7 - ii), tileY * TILE_SIZE + lineIdx, colors[colorVal])
                    }

                    lineIdx++;
                    if (lineIdx == TILE_SIZE) {
                        lineIdx = 0

                        tileX++
                        if (tileX == PALETTE_WIDTH) {
                            tileX = 0
                            tileY++
                        }
                    }
                }

                texture.draw(pixmap, 0, 0)
            }
        }, 0f, 1f) //lol
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }
}
