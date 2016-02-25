package com.kotcrab.xgbc

/** @author Kotcrab */
class OpCodesProcessor(private val emulator: Emulator) {
    private val cpu = emulator.cpu

    fun ld(reg: Int) {
        cpu.writeReg(reg, emulator.readNextPc())
    }
}
