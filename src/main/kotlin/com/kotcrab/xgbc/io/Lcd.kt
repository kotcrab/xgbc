package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.Emulator

/** @author Kotcrab */
class Lcd(private val emulator: Emulator) : IODevice {
    val gpu = emulator.gpu

    val LCDC = 0xFF40
    val STAT = 0xFF41
    val SCY = 0xFF42
    val SCX = 0xFF43
    val LY = 0xFF44
    val LYC = 0xFF45
    val DMA = 0xFF46
    val BGP = 0xFF47
    val OBP0 = 0xFF48
    val OBP1 = 0xFF49
    val WY = 0xFF4A
    val WX = 0xFF4B

    override fun register(registrar: (Int) -> Unit) {
        registrar.invoke(LCDC)
        registrar.invoke(STAT)
        registrar.invoke(SCY)
        registrar.invoke(SCX)
        registrar.invoke(LY)
        registrar.invoke(LYC)
        registrar.invoke(DMA)
        registrar.invoke(BGP)
        registrar.invoke(OBP0)
        registrar.invoke(OBP1)
        registrar.invoke(WY)
        registrar.invoke(WX)
    }

    override fun reset() {
    }

    override fun tick(cyclesElapsed: Int) {
    }

    override fun onRead(addr: Int) {
    }

    override fun onWrite(addr: Int, value: Byte) {
    }
}
