package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.Interrupt
import com.kotcrab.xgbc.isBitSet
import com.kotcrab.xgbc.toUnsignedInt

/** @author Kotcrab */
class Timer(private val emulator: Emulator) : IODevice {
    val TIMA = 0xFF05
    val TMA = 0xFF06
    val TAC = 0xFF07

    val clock00 = 1024//4096 khz
    val clock01 = 16//262144 khz
    val clock10 = 64//65536 khz
    val clock11 = 256//16384 khz

    //    var cycleUpdate = clock00
    var cycleUpdate = 1
    var cycleCounter = 0

    var tima = 0

    override fun register(registrar: (Int) -> Unit) {
        registrar.invoke(TIMA)
        registrar.invoke(TMA)
        registrar.invoke(TAC)
    }

    override fun tick(cyclesElapsed: Int) {
        if (emulator.read(TAC).isBitSet(2)) {
//            println("TAC:" + toHex(emulator.read(TAC)) + " TIMA:" + emulator.read(TIMA) + " TMA:" + emulator.read(TMA))
            cycleCounter += cyclesElapsed
            while (cycleCounter >= cycleUpdate) {
                cycleCounter -= cycleUpdate
                tima++
                if (tima >= 255) {
                    tima = emulator.read(TMA).toUnsignedInt()
                    emulator.interrupt(Interrupt.TIMER)
                }
                emulator.io.directWrite(TIMA, tima.toByte())
            }
        }
    }

    override fun reset() {
        cycleCounter = 0
        tima = 0
    }

    override fun onRead(addr: Int) {
    }

    override fun onWrite(addr: Int, value: Byte) {
        if (addr != TAC) return
        val timerCtl = value.toUnsignedInt() and 0x0003
        when (timerCtl) {
            0b00 -> cycleUpdate = clock00
            0b01 -> cycleUpdate = clock01
            0b10 -> cycleUpdate = clock10
            0b11 -> cycleUpdate = clock11
        }
    }
}
