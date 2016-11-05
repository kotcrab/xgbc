package com.kotcrab.xgbc.cpu

import com.kotcrab.xgbc.*

/** @author Kotcrab */
class OpCodesProcessor(private val emulator: Emulator, private val cpu: Cpu) {
    // 8 bit load operations

    /** Copies value from cpu's reg2 into reg1. */
    fun ld(reg1: Reg, reg2: Reg) {
        cpu.writeReg(reg1, cpu.readReg(reg2))
    }

    /** Loads 8 bit value from memory address pointed by reg16 into reg */
    fun ld8Reg16AddrToReg(reg: Reg, reg16: Reg16) {
        cpu.writeReg(reg, emulator.read(cpu.readReg(reg16)))
    }

    /** Saves 8 bit value from reg into memory address pointed by reg16 */
    fun ld8RegToReg16Addr(reg16: Reg16, reg: Reg) {
        emulator.write(cpu.readReg(reg16), cpu.readReg(reg))
    }

    /** Loads 8 bit value from addr into reg */
    fun ld8AddrToReg(reg: Reg, addr: Int) {
        cpu.writeReg(reg, emulator.read(addr))
    }

    /** Saves 8 bit value from reg to addr */
    fun ld8RegToAddr(addr: Int, reg: Reg) {
        emulator.write(addr, cpu.readReg(reg))
    }

    // Immediate 8 bit operations

    /** Loads immediate 8 bit value into cpu register. */
    fun ld8ImmValueToReg(reg: Reg) {
        cpu.writeReg(reg, emulator.read(cpu.pc + 1))
    }

    /** Loads 8 bit value from immediate address into reg */
    fun ld8ImmAddrToReg(reg: Reg) {
        val fromAddr = emulator.read16(cpu.pc + 1)
        syncTimer(8)
        ld8AddrToReg(reg, fromAddr)
    }

    /** Saves 8 bit value from reg into immediate address */
    fun ld8RegToImmAddr(reg: Reg) {
        val toAddr = emulator.read16(cpu.pc + 1)
        syncTimer(8)
        ld8RegToAddr(toAddr, reg)
    }

    // 16 bit load operations

    fun ld16ValueToReg16(reg16: Reg16, value: Int) {
        cpu.writeReg(reg16, value)
    }

    // Immediate 16 bit operations

    fun ld16ImmValueToReg(reg16: Reg16) {
        ld16ValueToReg16(reg16, emulator.read16(cpu.pc + 1))
    }

    // 8 bit ALU

    fun add(value: Int) {
        val regA = cpu.readRegInt(Reg.A)
        val result = regA + value

        if (result and 0xFF == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        if ((regA and 0xF) + (value and 0xF) and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)
        if ((regA and 0xFF) + (value and 0xFF) and 0x100 != 0) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Reg.A, result.toByte())
    }

    fun addReg(reg: Reg) {
        add(cpu.readRegInt(reg))
    }

    fun adc(value: Int) {
        val regA = cpu.readRegInt(Reg.A)
        val carry = cpu.isFlagSet(Cpu.FLAG_C).toInt()
        val result = regA + value + carry

        if (result and 0xFF == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        if ((regA and 0xF) + (value and 0xF) + carry and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)
        if ((regA and 0xFF) + (value and 0xFF) + carry and 0x100 != 0) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Reg.A, result.toByte())
    }

    fun adcReg(reg: Reg) {
        adc(cpu.readRegInt(reg))
    }

    fun sub(value: Int) {
        val regA = cpu.readRegInt(Reg.A)
        val result = regA - value

        if (result and 0xFF == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.setFlag(Cpu.FLAG_N)
        if ((regA and 0xF) - (value and 0xF) and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)
        if ((regA and 0xFF) - (value and 0xFF) and 0x100 != 0) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Reg.A, result.toByte())
    }

    fun subReg(reg: Reg) {
        sub(cpu.readRegInt(reg))
    }

    fun sbc(value: Int) {
        val regA = cpu.readRegInt(Reg.A)
        val carry = cpu.isFlagSet(Cpu.FLAG_C).toInt()
        val result = regA - value - carry

        if (result and 0xFF == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.setFlag(Cpu.FLAG_N)
        if ((regA and 0xF) - (value and 0xF) - carry and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)
        if ((regA and 0xFF) - (value and 0xFF) - carry and 0x100 != 0) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Reg.A, result.toByte())
    }

    fun sbcReg(reg: Reg) {
        sbc(cpu.readRegInt(reg))
    }

    fun and(value: Int) {
        val regA = cpu.readRegInt(Reg.A)
        val result = regA and value
        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.setFlag(Cpu.FLAG_H)
        cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Reg.A, result.toByte())
    }

    fun andReg(reg: Reg) {
        and(cpu.readRegInt(reg))
    }

    fun or(value: Int) {
        val regA = cpu.readRegInt(Reg.A)
        val result = regA or value
        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Reg.A, result.toByte())
    }

    fun orReg(reg: Reg) {
        or(cpu.readRegInt(reg))
    }

    fun xor(value: Int) {
        val regA = cpu.readRegInt(Reg.A)
        val result = regA xor value
        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Reg.A, result.toByte())
    }

    fun xorReg(reg: Reg) {
        xor(cpu.readRegInt(reg))
    }

    fun cp(value: Int) {
        val regA = cpu.readRegInt(Reg.A)
        val result = regA - value

        if (result and 0xFF == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.setFlag(Cpu.FLAG_N)
        if ((regA and 0xF) - (value and 0xF) and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)
        if (regA < value) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)
    }

    fun cpReg(reg: Reg) {
        cp(cpu.readRegInt(reg))
    }

    fun inc(reg: Reg) {
        val regValue = cpu.readRegInt(reg)
        val result = regValue + 1

        if (result and 0xFF == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        if ((regValue and 0xF) + (1 and 0xF) and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)

        cpu.writeReg(reg, result.toByte())
    }

    fun incHL() {
        val addr = cpu.readReg(Reg16.HL)
        val value = emulator.readInt(addr)
        val result = value + 1

        if (result and 0xFF == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        if ((value and 0xF) + (1 and 0xF) and 0x10 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)

        emulator.write(addr, result.toByte())
    }

    fun dec(reg: Reg) {
        val regValue = cpu.readRegInt(reg)
        val result = regValue - 1

        if (result and 0xFF == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.setFlag(Cpu.FLAG_N)
        if ((regValue and 0xF) + (-1 and 0xF) and 0x10 != 0) cpu.resetFlag(Cpu.FLAG_H) else cpu.setFlag(Cpu.FLAG_H)

        cpu.writeReg(reg, result.toByte())
    }

    fun decHL() {
        val addr = cpu.readReg(Reg16.HL)
        val value = emulator.readInt(addr)
        val result = value - 1

        if (result and 0xFF == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.setFlag(Cpu.FLAG_N)
        if ((value and 0xF) + (-1 and 0xF) and 0x10 != 0) cpu.resetFlag(Cpu.FLAG_H) else cpu.setFlag(Cpu.FLAG_H)

        emulator.write(addr, result.toByte())
    }

    // 16 bit ALU

    fun addHL(reg16: Reg16) {
        val regHL = cpu.readReg(Reg16.HL)
        val reg = cpu.readReg(reg16)
        val result = regHL + reg

        cpu.resetFlag(Cpu.FLAG_N)
        if ((regHL and 0xFFF) + (reg and 0xFFF) and 0x1000 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)
        if ((regHL and 0xFFFF) + (reg and 0xFFFF) and 0x10000 != 0) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Reg16.HL, result)
    }

    fun addHLSP() {
        val regHL = cpu.readReg(Reg16.HL)
        val result = regHL + cpu.sp

        cpu.resetFlag(Cpu.FLAG_N)
        if ((regHL and 0xFFF) + (cpu.sp and 0xFFF) and 0x1000 != 0) cpu.setFlag(Cpu.FLAG_H) else cpu.resetFlag(Cpu.FLAG_H)
        if ((regHL and 0xFFFF) + (cpu.sp and 0xFFFF) and 0x10000 != 0) cpu.setFlag(Cpu.FLAG_C) else cpu.resetFlag(Cpu.FLAG_C)

        cpu.writeReg(Reg16.HL, result)
    }

    /** Decrements reg16 */
    fun dec16(reg16: Reg16) {
        val value = cpu.readReg(reg16)
        cpu.writeReg(reg16, value - 1)
    }

    /** Increments reg16 */
    fun inc16(reg16: Reg16) {
        val value = cpu.readReg(reg16)
        cpu.writeReg(reg16, value + 1)
    }

    // Stack pointer push and pop

    fun push(addr: Int) {
        cpu.sp = cpu.sp - 2
        emulator.write16(cpu.sp, addr)
    }

    fun pop(): Int {
        val addr = emulator.read16(cpu.sp)
        cpu.sp = cpu.sp + 2
        return addr
    }

    fun pushReg(reg16: Reg16) {
        cpu.sp = cpu.sp - 2
        emulator.write16(cpu.sp, cpu.readReg(reg16))
    }

    fun popReg(reg16: Reg16) {
        cpu.writeReg(reg16, emulator.read16(cpu.sp))
        cpu.sp = cpu.sp + 2
    }

    // Others

    fun swap(b: Byte): Byte {
        val value = b.toUnsignedInt()
        val result = ((value and 0x0F shl 4) or (value and 0xF0 shr 4))
        if (result and 0xFF == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.resetFlag(Cpu.FLAG_C)
        return result.toByte()
    }

    fun swapReg(reg: Reg) {
        cpu.writeReg(reg, swap(cpu.readReg(reg)))
    }

    fun daa() {
        var regA = cpu.readRegInt(Reg.A)

        if (cpu.isFlagSet(Cpu.FLAG_N) == false) {
            if (cpu.isFlagSet(Cpu.FLAG_H) || (regA and 0xF) > 9) regA += 0x06
            if (cpu.isFlagSet(Cpu.FLAG_C) || regA > 0x9F) regA += 0x60
        } else {
            if (cpu.isFlagSet(Cpu.FLAG_H)) regA = (regA - 6) and 0xFF
            if (cpu.isFlagSet(Cpu.FLAG_C)) regA -= 0x60
        }

        cpu.resetFlag(Cpu.FLAG_H)
        cpu.resetFlag(Cpu.FLAG_Z)
        if ((regA and 0x100) == 0x100) {
            cpu.setFlag(Cpu.FLAG_C)
        }

        regA = regA and 0xFF

        if (regA == 0) cpu.setFlag(Cpu.FLAG_Z)

        cpu.writeReg(Reg.A, regA.toByte())
    }

    fun cpl() {
        cpu.writeReg(Reg.A, cpu.readRegInt(Reg.A).inv().toByte())
        cpu.setFlag(Cpu.FLAG_N)
        cpu.setFlag(Cpu.FLAG_H)
    }

    fun ccf() {
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.toggleFlag(Cpu.FLAG_C)
    }

    fun scf() {
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.setFlag(Cpu.FLAG_C)
    }

    // Jumps

    fun jp(): Boolean {
        cpu.pc = emulator.read16(cpu.pc + 1)
        return true
    }

    fun jpHL(): Boolean {
        cpu.pc = cpu.readReg(Reg16.HL)
        return true
    }

    fun jpNZ(): Boolean {
        return invokeIfZFlagReset { jp() }
    }

    fun jpZ(): Boolean {
        return invokeIfZFlagSet { jp() }

    }

    fun jpNC(): Boolean {
        return invokeIfCFlagReset { jp() }

    }

    fun jpC(): Boolean {
        return invokeIfCFlagSet { jp() }
    }

    fun jr(): Boolean {
        cpu.pc = cpu.pc + emulator.read(cpu.pc + 1) + 2 //jr is 2 bytes long which actually needs to be acknowledged here
        return true
    }

    fun jrNZ(): Boolean {
        return invokeIfZFlagReset { jr() }
    }

    fun jrZ(): Boolean {
        return invokeIfZFlagSet { jr() }
    }

    fun jrNC(): Boolean {
        return invokeIfCFlagReset { jr() }
    }

    fun jrC(): Boolean {
        return invokeIfCFlagSet { jr() }
    }

    // Calls

    fun call(): Boolean {
        push(cpu.pc + 3) //each CALL instructions is 3 bytes long
        jp()
        return true
    }

    fun callNZ(): Boolean {
        return invokeIfZFlagReset { call() }
    }

    fun callZ(): Boolean {
        return invokeIfZFlagSet { call() }
    }

    fun callNC(): Boolean {
        return invokeIfCFlagReset { call() }
    }

    fun callC(): Boolean {
        return invokeIfCFlagSet { call() }
    }

    // Restarts

    fun rst(addr: Int): Boolean {
        push(cpu.pc + 1) //each RST is one byte long
        cpu.pc = addr
        return true
    }

    // Returns

    fun ret(): Boolean {
        cpu.pc = pop()
        return true
    }

    fun reti(): Boolean {
        cpu.pc = pop()
        cpu.setImeFlagNow(true)
        return true
    }

    fun retNZ(): Boolean {
        return invokeIfZFlagReset { ret() }
    }

    fun retZ(): Boolean {
        return invokeIfZFlagSet { ret() }
    }

    fun retNC(): Boolean {
        return invokeIfCFlagReset { ret() }
    }

    fun retC(): Boolean {
        return invokeIfCFlagSet { ret() }
    }

    // Rotates and shifts

    fun rlc(byte: Byte, sefZFlagIfNeeded: Boolean): Byte {
        val result = byte.rotateLeft(1)

        if (sefZFlagIfNeeded) {
            if (result.toUnsignedInt() == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        } else {
            cpu.resetFlag(Cpu.FLAG_Z)
        }
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.setFlagState(Cpu.FLAG_C, byte.isBitSet(7))
        return result
    }

    fun rrc(byte: Byte, sefZFlagIfNeeded: Boolean): Byte {
        val result = byte.rotateRight(1)

        if (sefZFlagIfNeeded) {
            if (result.toUnsignedInt() == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        } else {
            cpu.resetFlag(Cpu.FLAG_Z)
        }
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.setFlagState(Cpu.FLAG_C, byte.isBitSet(0))
        return result
    }

    fun rl(byte: Byte, sefZFlagIfNeeded: Boolean): Byte {
        var result = byte.rotateLeft(1)

        result = result.setBitState(0, cpu.isFlagSet(Cpu.FLAG_C))

        if (sefZFlagIfNeeded) {
            if (result.toUnsignedInt() == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        } else {
            cpu.resetFlag(Cpu.FLAG_Z)
        }
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.setFlagState(Cpu.FLAG_C, byte.isBitSet(7))
        return result.toByte()
    }

    fun rr(byte: Byte, sefZFlagIfNeeded: Boolean): Byte {
        var result = byte.rotateRight(1)

        result = result.setBitState(7, cpu.isFlagSet(Cpu.FLAG_C))

        if (sefZFlagIfNeeded) {
            if (result.toUnsignedInt() == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        } else {
            cpu.resetFlag(Cpu.FLAG_Z)
        }
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.setFlagState(Cpu.FLAG_C, byte.isBitSet(0))
        return result.toByte()
    }

    fun rlcReg(reg: Reg, sefZFlagIfNeeded: Boolean = true) {
        val value = cpu.readReg(reg)
        cpu.writeReg(reg, rlc(value, sefZFlagIfNeeded))
    }

    fun rrcReg(reg: Reg, sefZFlagIfNeeded: Boolean = true) {
        val value = cpu.readReg(reg)
        cpu.writeReg(reg, rrc(value, sefZFlagIfNeeded))
    }

    fun rrReg(reg: Reg, sefZFlagIfNeeded: Boolean = true) {
        val value = cpu.readReg(reg)
        cpu.writeReg(reg, rr(value, sefZFlagIfNeeded))
    }

    fun rlReg(reg: Reg, sefZFlagIfNeeded: Boolean = true) {
        val value = cpu.readReg(reg)
        cpu.writeReg(reg, rl(value, sefZFlagIfNeeded))
    }

    fun sla(byte: Byte): Byte {
        var result = (byte.toInt() and 0xFF) shl 1
        result = result and 0xFF

        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.setFlagState(Cpu.FLAG_C, byte.isBitSet(7))

        return result.toByte()
    }

    fun srl(byte: Byte): Byte {
        var result = (byte.toInt() and 0xFF) shr 1
        result = result and 0xFF

        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.setFlagState(Cpu.FLAG_C, byte.isBitSet(0))

        return result.toByte()
    }

    fun sra(byte: Byte): Byte {
        var result = (byte.toInt() and 0xFF) ushr 1
        result = result and 0xFF
        result = result.toByte().setBitState(7, byte.isBitSet(7)).toUnsignedInt() //preserve msb

        if (result == 0) cpu.setFlag(Cpu.FLAG_Z) else cpu.resetFlag(Cpu.FLAG_Z)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.resetFlag(Cpu.FLAG_H)
        cpu.setFlagState(Cpu.FLAG_C, byte.isBitSet(0))

        return result.toByte()
    }

    fun slaReg(reg: Reg) {
        val value = cpu.readReg(reg)
        cpu.writeReg(reg, sla(value))
    }

    fun srlReg(reg: Reg) {
        val value = cpu.readReg(reg)
        cpu.writeReg(reg, srl(value))
    }

    fun sraReg(reg: Reg) {
        val value = cpu.readReg(reg)
        cpu.writeReg(reg, sra(value))
    }

    // Bit operations

    fun bit(bit: Int, byte: Byte) {
        cpu.setFlagState(Cpu.FLAG_Z, byte.isBitSet(bit) == false)
        cpu.resetFlag(Cpu.FLAG_N)
        cpu.setFlag(Cpu.FLAG_H)
    }

    fun set(bit: Int, byte: Byte): Byte {
        return byte.setBit(bit)
    }

    fun res(bit: Int, byte: Byte): Byte {
        return byte.resetBit(bit)
    }

    fun bitReg(bit: Int, reg: Reg) {
        bit(bit, cpu.readReg(reg))
    }

    fun bitHL(bit: Int) {
        val addr = cpu.readReg(Reg16.HL)
        syncTimer(4)
        bit(bit, emulator.read(addr))
    }

    fun setReg(bit: Int, reg: Reg) {
        val value = cpu.readReg(reg)
        cpu.writeReg(reg, set(bit, value))
    }

    fun resReg(bit: Int, reg: Reg) {
        val value = cpu.readReg(reg)
        cpu.writeReg(reg, res(bit, value))
    }

    // Internal util

    private fun invokeIfCFlagSet(runnable: () -> Boolean): Boolean {
        if (cpu.isFlagSet(Cpu.FLAG_C)) {
            return runnable.invoke()
        } else {
            return false
        }
    }

    private fun invokeIfCFlagReset(runnable: () -> Boolean): Boolean {
        if (cpu.isFlagSet(Cpu.FLAG_C) == false) {
            return runnable.invoke()
        } else {
            return false
        }
    }

    private fun invokeIfZFlagSet(runnable: () -> Boolean): Boolean {
        if (cpu.isFlagSet(Cpu.FLAG_Z)) {
            return runnable.invoke()
        } else {
            return false
        }
    }

    private fun invokeIfZFlagReset(runnable: () -> Boolean): Boolean {
        if (cpu.isFlagSet(Cpu.FLAG_Z) == false) {
            return runnable.invoke()
        } else {
            return false
        }
    }

    fun syncTimer(cycles: Int) {
        emulator.io.timer.sync(cycles)
    }
}
