package com.kotcrab.xgbc.ui

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.Interrupt
import com.kotcrab.xgbc.gdx.GdxGpu
import com.kotcrab.xgbc.io.Gpu

/** @author Kotcrab */

class VRAMWindow(val emulator: Emulator) : VisWindow("VRAM") {
    private companion object {
        val PALETTE_WIDTH = 16
        val PALETTE_HEIGHT = 16
    }

    private val gdxGpu = GdxGpu(emulator.gpu)

    private val pixmap = Pixmap(PALETTE_WIDTH * Gpu.TILE_SIZE, PALETTE_HEIGHT * Gpu.TILE_SIZE, Pixmap.Format.RGB888)
    private val texture = Texture(pixmap)

    var frameUsed = false

    init {
        val img = VisImage(TextureRegionDrawable(TextureRegion(texture)))
        add(img).size(256f, 256f)
        pack()

        emulator.interruptHandlers.add { interrupt ->
            if (interrupt == Interrupt.VBLANK && frameUsed == false) {
                frameUsed = true
                for (row in 0..PALETTE_WIDTH) {
                    for (column in 0..PALETTE_HEIGHT) {
                        gdxGpu.drawPattern0TileToPixmap(pixmap, row * Gpu.TILE_SIZE, column * Gpu.TILE_SIZE, column * 0x10 + row)
                    }
                }

                texture.draw(pixmap, 0, 0)
            }
        }

        setPosition(0f, 0f + width + 30)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        frameUsed = false
    }
}
