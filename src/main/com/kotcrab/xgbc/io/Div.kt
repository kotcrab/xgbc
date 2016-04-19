package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.Emulator

/** @author Kotcrab */
class Div(private val emulator: Emulator) : IODevice {
    val DIV = 0xFF04

    val cycleUpdate = 256  //16384 khz
    var cycleCounter = 0

    override fun register(registrar: (Int) -> Unit) {
        registrar.invoke(DIV)
    }

    override fun tick(cyclesElapsed: Int) {
        cycleCounter += cyclesElapsed
        var div = emulator.readInt(DIV)
        while (cycleCounter >= cycleUpdate) {
            cycleCounter -= cycleUpdate
            div += 1
            if (div > 0xFF) {
                div = 0
            }
        }
        emulator.io.directWrite(DIV, div.toByte())
    }

    override fun reset() {
        cycleCounter = 0
    }

    override fun onRead(addr: Int) {
    }

    override fun onWrite(addr: Int, value: Byte) {
        emulator.io.directWrite(DIV, 0)
    }
}
