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

    // 8 bit ALU

    fun add(value: Int) {
        val regA = cpu.readRegInt(Cpu.REG_A)
        val result = regA + value

        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        if ((regA and 0xF) + (value and 0xF) and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)
        if ((regA and 0xFF) + (value and 0xFF) and 0x100 != 0) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Cpu.REG_A, result.toByte())
    }

    fun addReg(reg: Int) {
        add(cpu.readRegInt(reg));
    }

    fun adc(value: Int) {
        add(value + if (cpu.isFlagSet(Cpu.FLAG_C)) 1 else 0)
    }

    fun adcReg(reg: Int) {
        add(cpu.readRegInt(reg) + if (cpu.isFlagSet(Cpu.FLAG_C)) 1 else 0)
    }

    fun sub(value: Int) {
        val regA = cpu.readRegInt(Cpu.REG_A)
        val result = regA - value

        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.setFlag(Cpu.FLAG_N)
        if ((regA and 0xF) - (value and 0xF) and 0x10 != 0) cpu.resetFlag(Cpu.FLAG_H) else cpu.setFlag(Cpu.FLAG_H)
        if ((regA and 0xFF) - (value and 0xFF) and 0x100 != 0) cpu.resetFlag(Cpu.FLAG_C) else cpu.setFlag(Cpu.FLAG_C)

        cpu.writeReg(Cpu.REG_A, result.toByte())
    }

    fun subReg(reg: Int) {
        sub(cpu.readRegInt(reg));
    }

    fun sbc(value: Int) {
        sbc(value + if (cpu.isFlagSet(Cpu.FLAG_C)) 1 else 0)
    }

    fun sbcReg(reg: Int) {
        sbc(cpu.readRegInt(reg) + if (cpu.isFlagSet(Cpu.FLAG_C)) 1 else 0)
    }

    fun and(value: Int) {
        val regA = cpu.readRegInt(Cpu.REG_A)
        val result = regA and value;
        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.setFlag(Cpu.FLAG_H)
        cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Cpu.REG_A, result.toByte())
    }

    fun andReg(reg: Int) {
        and(cpu.readRegInt(reg))
    }

    fun or(value: Int) {
        val regA = cpu.readRegInt(Cpu.REG_A)
        val result = regA or value;
        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Cpu.REG_A, result.toByte())
    }

    fun orReg(reg: Int) {
        or(cpu.readRegInt(reg))
    }

    fun xor(value: Int) {
        val regA = cpu.readRegInt(Cpu.REG_A)
        val result = regA xor value;
        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Cpu.REG_A, result.toByte())
    }

    fun xorReg(reg: Int) {
        xor(cpu.readRegInt(reg))
    }

    fun cp(value: Int) {
        val regA = cpu.readRegInt(Cpu.REG_A)
        val result = regA - value

        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.setFlag(Cpu.FLAG_N)
        if ((regA and 0xF) - (value and 0xF) and 0x10 != 0) cpu.resetFlag(Cpu.FLAG_H) else cpu.setFlag(Cpu.FLAG_H)
        if (regA < value) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)
    }

    fun cpReg(reg: Int) {
        cp(cpu.readRegInt(reg))
    }

    fun inc(reg: Int) {
        val regValue = cpu.readRegInt(reg)
        val result = regValue + 1

        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        if ((regValue and 0xF) + (1 and 0xF) and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)

        cpu.writeReg(reg, result.toByte())
    }

    fun incHL() {
        val addr = cpu.readReg16(Cpu.REG_HL);
        val value = emulator.readInt(addr)
        val result = value + 1

        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        if ((value and 0xF) + (1 and 0xF) and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)

        emulator.write(addr, result.toByte())
    }

    fun dec(reg: Int) {
        val regValue = cpu.readRegInt(reg)
        val result = regValue - 1

        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.setFlag(Cpu.FLAG_N)
        if ((regValue and 0xF) + (-1 and 0xF) and 0x10 != 0) cpu.resetFlag(Cpu.FLAG_H) else cpu.setFlag(Cpu.FLAG_H)

        cpu.writeReg(reg, result.toByte())
    }

    fun decHL() {
        val addr = cpu.readReg16(Cpu.REG_HL);
        val value = emulator.readInt(addr)
        val result = value - 1

        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.setFlag(Cpu.FLAG_N)
        if ((value and 0xF) + (-1 and 0xF) and 0x10 != 0) cpu.resetFlag(Cpu.FLAG_H) else cpu.setFlag(Cpu.FLAG_H)

        emulator.write(addr, result.toByte())
    }

    // 16 bit ALU

    fun addHL(reg16: Int) {
        val regHL = cpu.readReg16(Cpu.REG_HL)
        val reg = cpu.readRegInt(reg16)
        val result = regHL + reg

        cpu.resetFlag(Cpu.FLAG_N)
        if ((regHL and 0xF) + (reg and 0xF) and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)
        if ((regHL and 0xFF) + (reg and 0xFF) and 0x100 != 0) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg16(Cpu.REG_HL, result)
    }

    fun addHLSP() {
        val regHL = cpu.readReg16(Cpu.REG_HL)
        val result = regHL + cpu.sp

        cpu.resetFlag(Cpu.FLAG_N)
        if ((regHL and 0xF) + (cpu.sp and 0xF) and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)
        if ((regHL and 0xFF) + (cpu.sp and 0xFF) and 0x100 != 0) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg16(Cpu.REG_HL, result)
    }

    /** Decrements reg16 */
    fun dec16(reg16: Int) {
        val value = cpu.readReg16(reg16);
        cpu.writeReg16(reg16, value - 1);
    }

    /** Increments reg16 */
    fun inc16(reg16: Int) {
        val value = cpu.readReg16(reg16);
        cpu.writeReg16(reg16, value + 1);
    }

    // Stack pointer push and pop

    fun push(reg16: Int) {
        cpu.sp = cpu.sp - 2
        emulator.write16(cpu.sp, cpu.readReg16(reg16))
    }

    fun pop(reg16: Int) {
        cpu.writeReg16(reg16, emulator.read16(cpu.sp))
        cpu.sp = cpu.sp + 2
    }

    // Others

    fun daa() {
        var regA = cpu.readRegInt(Cpu.REG_A)

        if (cpu.isFlagSet(Cpu.FLAG_N)) {
            if (cpu.isFlagSet(Cpu.FLAG_H) || (regA and 0xF) > 9) regA += 0x06;
            if (cpu.isFlagSet(Cpu.FLAG_C) || regA > 0x9F) regA += 0x60
        } else {
            if (cpu.isFlagSet(Cpu.FLAG_H)) regA = (regA - 6) and 0xFF;
            if (cpu.isFlagSet(Cpu.FLAG_C)) regA -= 0x60;
        }

        cpu.resetFlag(Cpu.FLAG_H)
        if ((regA and 0x100) == 0x100) cpu.setFlag(Cpu.FLAG_C)
        else
            cpu.setFlag(Cpu.FLAG_C)

        regA = regA and 0xFF;

        if (regA == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)

        cpu.writeReg(Cpu.REG_A, regA.toByte());


    }
}
