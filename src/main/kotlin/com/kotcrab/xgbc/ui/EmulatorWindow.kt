package com.kotcrab.xgbc.ui

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.gdx.GdxGpu
import com.kotcrab.xgbc.io.Gpu
import com.kotcrab.xgbc.toUnsignedInt

/** @author Kotcrab */

class EmulatorWindow(val emulator: Emulator) : VisWindow("Emulator") {
    private companion object {
        val PALETTE_WIDTH = 32
        val PALETTE_HEIGHT = 32
    }

    private val pixmap = Pixmap(PALETTE_WIDTH * Gpu.TILE_SIZE, PALETTE_HEIGHT * Gpu.TILE_SIZE, Pixmap.Format.RGB888)
    private val texture = Texture(pixmap)

    private val gpu = emulator.gpu
    private val lcd = emulator.io.lcd
    private val gdxGpu = GdxGpu(gpu)

    init {
        val img = VisImage(TextureRegionDrawable(TextureRegion(texture)))
        add(img).size(256f, 256f)
        emulator.lcdTransferHandler = {
            val tileMapStart = lcd.getBgTileMapDataAddr()
            val patternDataAddr = Gpu.PATTERN_TABLE_0
            for ((index, addr) in (tileMapStart..tileMapStart + Gpu.TIME_MAP_DATA_SIZE).withIndex()) {
                val tileId = if (patternDataAddr == Gpu.PATTERN_TABLE_0) emulator.read(addr).toUnsignedInt() else emulator.read(addr).toInt()
                val column = index / 32
                val row = index - column * 32
                if (column == lcd.scanLine)
                    gdxGpu.drawPatternTileToPixmap(pixmap, row * Gpu.TILE_SIZE, column * Gpu.TILE_SIZE, patternDataAddr, tileId)
            }
            texture.draw(pixmap, 0, 0)
        }
        pack()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }
}
