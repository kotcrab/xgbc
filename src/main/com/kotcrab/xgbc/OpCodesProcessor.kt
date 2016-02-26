package com.kotcrab.xgbc

/** @author Kotcrab */
class OpCodesProcessor(private val emulator: Emulator) {
    private val cpu = emulator.cpu

    // 8 bit load operations

    /** Copies value from cpu's reg2 into reg1. */
    fun ld8RegToReg(reg1: Int, reg2: Int) {
        cpu.writeReg(reg1, cpu.readReg(reg2))
    }

    /** Loads 8 bit value from memory address pointed by reg16 into reg */
    fun ld8Reg16AddrToReg(reg: Int, reg16: Int) {
        cpu.writeReg(reg, emulator.read(cpu.readReg16(reg16)))
    }

    /** Saves 8 bit value from reg into memory address pointed by reg16 */
    fun ld8RegToReg16Addr(reg16: Int, reg: Int) {
        emulator.write(cpu.readReg16(reg16), cpu.readReg(reg))
    }

    /** Loads 8 bit value from addr into reg */
    fun ld8AddrToReg(reg: Int, addr: Int) {
        cpu.writeReg(reg, emulator.read(addr))
    }

    /** Saves 8 bit value from reg to addr */
    fun ld8RegToAddr(addr: Int, reg: Int) {
        emulator.write(addr, cpu.readReg(reg))
    }

    // Immediate 8 bit operations

    /** Loads immediate 8 bit value into cpu register. */
    fun ld8ImmValueToReg(reg: Int) {
        cpu.writeReg(reg, emulator.read(cpu.pc + 1))
    }

    /** Loads 8 bit value from immediate address into reg */
    fun ld8ImmAddrToReg(reg: Int) {
        val fromAddr = emulator.read16(cpu.pc + 1);
        ld8AddrToReg(reg, fromAddr)
    }

    /** Saves 8 bit value from reg into immediate address */
    fun ld8RegToImmAddr(reg: Int) {
        val toAddr = emulator.read16(cpu.pc + 1);
        ld8RegToAddr(toAddr, reg);
    }

    // 16 bit load operations

    fun ld16ValueToReg16(reg16: Int, value: Int) {
        cpu.writeReg16(reg16, value)
    }

    // Immediate 16 bit operations

    fun ld16ImmValueToReg(reg16: Int) {
        ld16ValueToReg16(reg16, emulator.read16(cpu.pc + 1))
    }

    // 16 bit ALU

    /** Decrements reg16 */
    fun dec16(reg16: Int) {
        val value = cpu.readReg16(reg16);
        cpu.writeReg16(reg16, value - 1);
    }

    fun inc16(reg16: Int) {
        val value = cpu.readReg16(reg16);
        cpu.writeReg16(reg16, value + 1);
    }
}
