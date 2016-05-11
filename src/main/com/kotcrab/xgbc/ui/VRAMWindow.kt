package com.kotcrab.xgbc.ui

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Timer
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.gdx.GdxGpu

/** @author Kotcrab */

class VRAMWindow(val emulator: Emulator) : VisWindow("VRAM") {
    private companion object {
        val TILE_SIZE = 8
        val PALETTE_WIDTH = 16
        val PALETTE_HEIGHT = 16
    }

    private val gdxGpu = GdxGpu(emulator.gpu)

    private val pixmap = Pixmap(PALETTE_WIDTH * TILE_SIZE, PALETTE_HEIGHT * TILE_SIZE, Pixmap.Format.RGB888)
    private val texture = Texture(pixmap)

    init {
        val img = VisImage(TextureRegionDrawable(TextureRegion(texture)));
        add(img).size(256f, 256f)
        pack()

        Timer.schedule(object : Timer.Task() {
            override fun run() {
                for (row in 0..PALETTE_WIDTH) {
                    for (column in 0..PALETTE_HEIGHT) {
                        gdxGpu.drawPattern1TileToPixmap(pixmap, row * TILE_SIZE, column * TILE_SIZE, column * 0x10 + row)
                    }
                }

                texture.draw(pixmap, 0, 0)
            }
        }, 0f, 1f) //just for testing until GPU interrupt are implemented
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }
}
