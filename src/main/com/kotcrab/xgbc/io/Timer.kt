package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.isBitSet
import com.kotcrab.xgbc.toUnsignedInt

/** @author Kotcrab */
class Timer(private val emulator: Emulator) : IODevice {
    val TIMA = 0xFF05
    val TMA = 0xFF06
    val TAC = 0xFF07

    val clock00 = 4096
    val clock01 = 262144
    val clock10 = 65536
    val clock11 = 16384

    var tickCounter = 0
    val tickUpdate = 4096

    var tima = 0

    override fun register(registrar: (Int) -> Unit) {
        registrar.invoke(TIMA)
        registrar.invoke(TMA)
        registrar.invoke(TAC)
    }

    override fun tick() {
        if(emulator.read(TAC).isBitSet(2)) {
            println("tick")
            tickCounter++
            if (tickCounter >= tickUpdate) {
                tickCounter = 0
                tima++
                if (tima > 255) {
                    tima = emulator.read(TMA).toUnsignedInt()
                }
                emulator.io.directWrite(TIMA, tima.toByte())
            }
        }
    }

    override fun reset() {
    }

    override fun onRead(addr: Int) {
    }

    override fun onWrite(addr: Int, value: Byte) {
    }
}
