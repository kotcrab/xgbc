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

    val clock00 = 1024 //4096 hz
    val clock01 = 16   //262144 hz
    val clock10 = 64   //65536 hz
    val clock11 = 256  //16384 hz

    var cycleUpdate = clock00
    var cycleCounter = 0
    var cycleSync = 0

    override fun register(registrar: (Int) -> Unit) {
        registrar.invoke(TIMA)
        registrar.invoke(TMA)
        registrar.invoke(TAC)
    }

    override fun tick(cyclesElapsed: Int) {
        if (emulator.read(TAC).isBitSet(2)) {
            cycleCounter += (cyclesElapsed - cycleSync)
            cycleSync = 0
            var tima = emulator.readInt(TIMA)
            while (cycleCounter >= cycleUpdate) {
                //println("TAC:" + toHex(emulator.read(TAC)) + " TIMA:" + toHex(emulator.read(TIMA)) + " TMA:" + toHex(emulator.read(TMA)))
                cycleCounter -= cycleUpdate
                tima++

                if (tima > 0xFF) {
                    tima = emulator.readInt(TMA)
                    emulator.interrupt(Interrupt.TIMER)
                }
            }

            emulator.io.directWrite(TIMA, tima.toByte())
        }
    }

    fun sync(cycles: Int) {
        if (emulator.read(TAC).isBitSet(2)) {
            tick(cycles)
            cycleSync += cycles
        }
    }

    override fun reset() {
        cycleUpdate = clock00
        cycleCounter = 0
    }

    override fun onRead(addr: Int) {
    }

    override fun onWrite(addr: Int, value: Byte) {
        //println("TAC:" + toHex(emulator.read(TAC)) + " TIMA:" + toHex(emulator.read(TIMA)) + " TMA:" + toHex(emulator.read(TMA)))
        if (addr != TAC) return
        val timerClk = value.toUnsignedInt() and 0b11
        when (timerClk) {
            0b00 -> cycleUpdate = clock00
            0b01 -> cycleUpdate = clock01
            0b10 -> cycleUpdate = clock10
            0b11 -> cycleUpdate = clock11
        }
    }
}
