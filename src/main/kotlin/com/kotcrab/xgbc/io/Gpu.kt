package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.toInt

/** @author Kotcrab */
class Gpu(private val emulator: Emulator) {
    companion object {
        val TILE_SIZE = 8
        val TILE_BYTE_SIZE = 16
        val PATTERN_TABLE_0 = 0x8000
    }

    val vram: ByteArray = ByteArray(0x2000)

    fun reset() {
        vram.fill(0)
    }

    fun readTilePatternTable0(tileId: Int, buffer: IntArray): IntArray {
        val tileStart = PATTERN_TABLE_0 + (tileId * TILE_BYTE_SIZE)
        return readTile(tileStart, buffer)
    }

    fun readTile(tileStartAddr: Int, buffer: IntArray): IntArray {
        if (buffer.size < 64) throw IllegalStateException("buffer must be at least 64 bytes")

        var bufferIdx = 0
        var lineIdx = 0

        for (i in tileStartAddr..tileStartAddr + TILE_BYTE_SIZE - 1 step 2) {
            val byte = emulator.readInt(i)
            val byte2 = emulator.readInt(i + 1)

            for (ii in 0..7) {
                val colorLSB = byte and (1 shl ii) != 0
                val colorMSB = ((byte2 and (1 shl ii)) shl 1) != 0
                val colorVal = colorLSB.toInt() or colorMSB.toInt()
                buffer[bufferIdx++] = colorVal
            }
            lineIdx++
        }

        return buffer
    }
}
