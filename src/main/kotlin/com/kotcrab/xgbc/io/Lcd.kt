package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.*

/** @author Kotcrab */
class Lcd(private val emulator: Emulator) : IODevice {
    val gpu = emulator.gpu

    val LCDC = 0xFF40
    val STAT = 0xFF41
    val SCY = 0xFF42
    val SCX = 0xFF43
    val LY = 0xFF44
    val LYC = 0xFF45 //TODO compare with LY
    val DMA = 0xFF46
    val BGP = 0xFF47
    val OBP0 = 0xFF48
    val OBP1 = 0xFF49
    val WY = 0xFF4A
    val WX = 0xFF4B

    //val cycleVBlank = 70224  //59.7 hz

    var scanLine = 0
    var mode = Mode.OAM_SEARCH
    var cycleCounter = 0

    override fun register(registrar: (Int) -> Unit) {
        registrar(LCDC)
        registrar(STAT)
        registrar(SCY)
        registrar(SCX)
        registrar(LY)
        registrar(LYC)
        registrar(DMA)
        registrar(BGP)
        registrar(OBP0)
        registrar(OBP1)
        registrar(WY)
        registrar(WX)
    }

    override fun reset() {
    }

    override fun tick(cyclesElapsed: Int) {
        cycleCounter += cyclesElapsed
        while (cycleCounter >= mode.cycles) {
            cycleCounter -= mode.cycles

            when (mode) {
                Mode.OAM_SEARCH -> {
                    mode = Mode.LCD_TRANSFER
                    emulator.lcdTransferHandler.invoke()
                    scanLine++
                }
                Mode.LCD_TRANSFER -> {
                    mode = Mode.HBLANK
                }
                Mode.HBLANK -> {
                    if (scanLine == 144) {
                        mode = Mode.VBLANK
                    } else {
                        mode = Mode.OAM_SEARCH
                    }
                    emulator.io.directWrite(LY, scanLine.toByte())
                }
                Mode.VBLANK -> {
                    if (scanLine == 144) {
                        emulator.interrupt(Interrupt.VBLANK)
                    }

                    scanLine++
                    if (scanLine == 154) {
                        mode = Mode.OAM_SEARCH
                        scanLine = 0
                    }
                    emulator.io.directWrite(LY, scanLine.toByte())
                }
            }

            var stat = emulator.read(STAT).setBitState(2, emulator.read(LYC) == emulator.read(LY)).toUnsignedInt()
            stat = stat and 0b11111100
            when (mode) {
                Mode.OAM_SEARCH -> {
                    stat = stat or 0b10
                    if (stat.toByte().isBitSet(5)) emulator.interrupt(Interrupt.LCDC)
                }
                Mode.LCD_TRANSFER -> {
                    stat = stat or 0b11
                }
                Mode.HBLANK -> {
                    stat = stat or 0b00
                    if (stat.toByte().isBitSet(3)) emulator.interrupt(Interrupt.LCDC)
                }
                Mode.VBLANK -> {
                    stat = stat or 0b01
                    if (stat.toByte().isBitSet(4)) emulator.interrupt(Interrupt.LCDC)
                }
            }
            val statByte = stat.toByte()
            //if (statByte.isBitSet(6) && statByte.isBitSet(2)) emulator.interrupt(Interrupt.LCDC)
            emulator.io.directWrite(STAT, statByte)
        }
    }

    override fun onRead(addr: Int) {

    }

    override fun onWrite(addr: Int, value: Byte) {
        if (addr == LY) {
            emulator.io.directWrite(LY, 0)
        }
    }

    fun isLcdEnabled(): Boolean {
        return emulator.read(LCDC).isBitSet(7) == true
    }

    fun getBgTileMapDataAddr(): Int {
        return if (emulator.read(LCDC).isBitSet(6)) Gpu.TILE_MAP_DATA_1 else Gpu.TILE_MAP_DATA_0
    }

    fun isWindowDisplayEnabled(): Boolean {
        return emulator.read(LCDC).isBitSet(5) == true
    }

    fun getPatternDataAddr(): Int {
        return if (emulator.read(LCDC).isBitSet(4)) Gpu.PATTERN_TABLE_1 else Gpu.PATTERN_TABLE_0
    }

    fun getWindowTileMapDataAddr(): Int {
        return if (emulator.read(LCDC).isBitSet(3)) Gpu.TILE_MAP_DATA_1 else Gpu.TILE_MAP_DATA_0
    }

    fun isSpriteDisplayEnabled(): Boolean {
        return emulator.read(LCDC).isBitSet(1) == true
    }

    fun isBgAndWindowDisplayEnabled(): Boolean {
        return emulator.read(LCDC).isBitSet(0) == true
    }

    enum class Mode(val mode: Byte, val cycles: Int) {
        HBLANK(0, 204),
        OAM_SEARCH(2, 80),
        LCD_TRANSFER(3, 172),
        VBLANK(1, 456);
    }
}
