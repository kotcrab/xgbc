package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.Interrupt

/** @author Kotcrab */
class SerialPort(private val emulator: Emulator) : IODevice {
    val SB = 0xFF01
    val SC = 0xFF02

    var cycleCounter = 0
    val cycleUpdate = 512 //~8102 khz

    override fun register(registrar: (Int) -> Unit) {
        registrar.invoke(SB)
        registrar.invoke(SC)
    }

    override fun tick(cyclesElapsed: Int) {
        cycleCounter += cyclesElapsed
        if (cycleCounter >= cycleUpdate) {
            cycleCounter -= cycleUpdate
            val sc = emulator.readInt(SC);
            if (sc == 0x81) {
                print(emulator.read(SB).toChar())
                emulator.write(SB, 0xFF)
                emulator.write(SC, 0x01)
                emulator.interrupt(Interrupt.SERIAL)
            }
        }
    }

    override fun reset() {
        cycleCounter = 0
    }

    override fun onRead(addr: Int) {
    }

    override fun onWrite(addr: Int, value: Byte) {
    }
}
