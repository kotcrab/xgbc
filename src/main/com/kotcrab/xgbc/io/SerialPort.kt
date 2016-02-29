package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.Emulator

/** @author Kotcrab */
class SerialPort(private val emulator: Emulator) : IODevice {
    val SB = 0xFF01
    val SC = 0xFF02

    var tickCounter = 0
    val tickUpdate = 8192

    override fun register(registrar: (Int) -> Unit) {
        registrar.invoke(SB)
        registrar.invoke(SC)
    }

    override fun tick() {
        tickCounter++
        if (tickCounter >= tickUpdate) {
            tickCounter = 0
//            println(emulator.readInt(SB).toChar())
            emulator.write(SB, 0xFF)
            emulator.write(SC, 0)

        }
    }

    override fun reset() {
    }

    override fun onRead(addr: Int) {
//        println("serial read! " + toHex(addr))
    }

    override fun onWrite(addr: Int, value: Byte) {
//        println("serial write! " + toHex(addr))
    }
}
