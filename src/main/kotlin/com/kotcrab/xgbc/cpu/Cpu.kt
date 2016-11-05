package com.kotcrab.xgbc.cpu

import com.kotcrab.xgbc.*

/** @author Kotcrab */
class Cpu(private val emulator: Emulator) {
    var sp: Int = 0 //stack pointer
    var pc: Int = 0 //program counter
    var cycle: Long = 0
        private set
    var halt: Boolean = false
    private val regs: ByteArray = ByteArray(8)
    var ime = false //interrupt master enable
        private set
    private var targetIme = false
    private var changeImeState = ImeState.IDLE

    val op = arrayOfNulls<Instr>(256)
    val extOp = arrayOfNulls<Instr>(256)

    private var opProc: OpCodesProcessor

    init {
        opProc = OpCodesProcessor(emulator, this)
        generateOpCodes(emulator, this, opProc, op)
        generateExtOpCodes(emulator, this, opProc, extOp)
    }

    fun readReg(reg: Reg): Byte {
        return regs[reg.index]
    }

    fun readReg(reg16: Reg16): Int {
        val r1 = readRegInt(reg16.highReg)
        val r2 = readRegInt(reg16.lowReg)
        return ((r1 shl 8) or r2)
    }

    fun readRegInt(reg: Reg): Int {
        return readReg(reg).toUnsignedInt()
    }

    fun writeReg(reg: Reg, value: Byte) {
        var setValue = value
        if (reg == Reg.F) {
            //reset four lsb bits
            setValue = (value.toUnsignedInt() and 0xF0).toByte()
        }
        regs[reg.index] = setValue
        emulator.debuggerListener.onRegisterWrite(reg, setValue)
    }

    fun writeReg(reg16: Reg16, value: Int) {
        writeReg(reg16.highReg, (value ushr 8).toByte())
        writeReg(reg16.lowReg, (value).toByte())
    }

    fun setImeFlag(ime: Boolean) {
        if (this.ime == ime) return
        targetIme = ime
        changeImeState = ImeState.CHANGE_AFTER_NEXT
    }

    fun setImeFlagNow(ime: Boolean) {
        this.ime = ime
    }

    fun setFlag(flag: Flag) {
        var flagReg = readRegInt(Reg.F)
        flagReg = flagReg or (1 shl flag.bit)
        writeReg(Reg.F, flagReg.toByte())
    }

    fun resetFlag(flag: Flag) {
        var flagReg = readRegInt(Reg.F)
        flagReg = flagReg and (1 shl flag.bit).inv()
        writeReg(Reg.F, flagReg.toByte())
    }

    fun toggleFlag(flag: Flag) {
        var flagReg = readRegInt(Reg.F)
        flagReg = flagReg xor (1 shl flag.bit)
        writeReg(Reg.F, flagReg.toByte())
    }

    fun setFlagState(flag: Flag, flagState: Boolean) {
        if ((flagState && isFlagSet(flag) == false) || (flagState == false && isFlagSet(flag))) {
            toggleFlag(flag)
        }
    }

    fun isFlagSet(flag: Flag): Boolean {
        val flagReg = readRegInt(Reg.F)
        return flagReg and (1 shl flag.bit) != 0
    }

    fun tick() {
        processInterrupts()

        if (halt) {
            cycle += 1
            emulator.debuggerListener.onCpuTick(pc, pc)
            return
        }

        val oldPc = pc
        val opcode = emulator.readInt(pc)

        val instr: Instr?
        if (opcode == 0xCB) {
            instr = emulator.cpu.extOp[emulator.readInt(pc + 1)]
        } else {
            instr = emulator.cpu.op[opcode]
        }

        if (instr == null) throw EmulatorException("Illegal opcode: ${toHex(opcode.toByte())} at ${toHex(pc)}")

        if (instr is JmpInstr) {
            val result = instr.op.invoke()
            if (result == false) {
                pc += instr.len
                cycle += instr.cycles
            } else {
                cycle += instr.cyclesIfTaken
            }
        } else {
            instr.op.invoke()
            if (oldPc != pc) {
                println("Warn: PC modification by non JmpInstr will be ignored, opcode: ${toHex(opcode.toByte())}")
            }
            pc += instr.len
            cycle += instr.cycles
        }

        when (changeImeState) {
            ImeState.IDLE -> {
            }
            ImeState.CHANGE_AFTER_NEXT -> {
                changeImeState = ImeState.CHANGE_IME
            }
            ImeState.CHANGE_IME -> {
                ime = targetIme
                changeImeState = ImeState.IDLE
            }
        }

        emulator.debuggerListener.onCpuTick(oldPc, pc)
    }

    private fun processInterrupts() {
        val ie = emulator.read(Emulator.REG_IE)
        val if_ = emulator.read(Emulator.REG_IF)

        for (interrupt in Interrupt.values()) {
            if (ie.isBitSet(interrupt.interruptBit) && if_.isBitSet(interrupt.interruptBit)) {
                halt = false
                if (ime == false) return
                emulator.write(Emulator.REG_IF, emulator.read(Emulator.REG_IF).resetBit(interrupt.interruptBit))
                ime = false
                opProc.push(pc)
                cycle += 16
                pc = interrupt.addr
                break
            }
        }
    }

    private enum class ImeState {
        IDLE,
        CHANGE_AFTER_NEXT,
        CHANGE_IME,
    }
}

enum class Reg(val index: Int) {
    A(0), F(1),
    B(2), C(3),
    D(4), E(5),
    H(6), L(7)
}

enum class Reg16(val highReg: Reg, val lowReg: Reg) {
    AF(Reg.A, Reg.F),
    BC(Reg.B, Reg.C),
    DE(Reg.D, Reg.E),
    HL(Reg.H, Reg.L)
}

enum class Flag(val bit: Int) {
    Z(7), N(6), H(5), C(4)
}

open class Instr(val len: Int,
                 val cycles: Int,
                 val mnemonic: String,
                 val op: () -> Any?) {
}

class VoidInstr(len: Int,
                cycles: Int,
                mnemonic: String,
                op: () -> Unit) : Instr(len, cycles, mnemonic, op)

class JmpInstr(len: Int,
               val cyclesIfTaken: Int,
               cycles: Int,
               mnemonic: String,
               op: () -> Boolean) : Instr(len, cycles, mnemonic, op) {
}
