package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.Emulator

/** @author Kotcrab */
class Div(private val emulator: Emulator) : IODevice {
    val DIV = 0xFF04

    var tickCounter = 0
    val tickUpdate = 16384

    override fun register(registrar: (Int) -> Unit) {
        registrar.invoke(DIV)
    }

    override fun tick(cyclesElapsed: Int) {
        tickCounter++
        if (tickCounter >= tickUpdate) {
            tickCounter = 0
            emulator.io.directWrite(DIV, (emulator.readInt(DIV) + 1).toByte())
        }
    }

    override fun reset() {
    }

    override fun onRead(addr: Int) {
    }

    override fun onWrite(addr: Int, value: Byte) {
        emulator.write(addr, 0)
    }
}
