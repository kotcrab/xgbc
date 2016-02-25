package com.kotcrab.xgbc

/** @author Kotcrab */
class OpCodesProcessor(private val emulator: Emulator) {
    private val cpu = emulator.cpu

    /** Loads 8 bit value into cpu register. */
    fun ld8(reg: Int, value: Byte) {
        cpu.writeReg(reg, value)
    }

    /** Loads immediate 8 bit value into cpu register. */
    fun ld8Imm(reg: Int) {
        ld8(reg, emulator.read(cpu.pc + 1))
    }

    /** Copies value from cpu's reg2 into reg1. */
    fun ld8(reg1: Int, reg2: Int) {
        cpu.writeReg(reg1, cpu.readReg(reg2))
    }

    /** Loads 8 bit value from memory address pointed by reg16 into reg */
    fun ld8FromReg16Addr(reg: Int, reg16: Int) {
        cpu.writeReg(reg, emulator.read(cpu.readReg16(reg16)))
    }

    /** Loads 8 bit value from reg into memory address pointed by reg16 */
    fun ld8ToReg16Addr(reg16: Int, reg: Int) {
        emulator.write(cpu.readReg16(reg16), cpu.readReg(reg))
    }

    /** Loads 8 bit value from addr into reg */
    fun ld8FromAddr(reg: Int, addr: Int) {
        cpu.writeReg(reg, emulator.read(addr))
    }

    /** Loads 8 bit value from immediate address into reg */
    fun ld8Imm16(reg: Int) {
        val fromAddr = emulator.read16(cpu.pc + 1);
        ld8FromAddr(reg, emulator.read16(fromAddr))
    }
}
