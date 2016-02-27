package com.kotcrab.xgbc

/** @author Kotcrab */
class Cpu(private val emulator: Emulator) {
    companion object {
        const val REG_A = 0
        const val REG_F = 1
        const val REG_B = 2
        const val REG_C = 3
        const val REG_D = 4
        const val REG_E = 5
        const val REG_H = 6
        const val REG_L = 7

        const val REG_AF = 0
        const val REG_BC = 2
        const val REG_DE = 4
        const val REG_HL = 6

        const val FLAG_Z = 7
        const val FLAG_N = 6
        const val FLAG_H = 5
        const val FLAG_C = 4
    }

    var sp: Int = 0 //stack pointer
    var pc: Int = 0 //program counter
    private val regs: ByteArray = ByteArray(8)

    val op = arrayOfNulls<Instr>(256)
    val extOp = arrayOfNulls<Instr>(256)

    private lateinit var opProc: OpCodesProcessor;

    init {
        opProc = OpCodesProcessor(emulator, this)
        generateOpCodes(emulator, this, opProc, op)
        generateExtOpCodes(opProc, extOp)
    }

    fun readReg(reg: Int): Byte {
        return regs[reg]
    }

    fun readRegInt(reg: Int): Int {
        return readReg(reg).toInt() and 0xFF
    }

    fun writeReg(reg: Int, value: Byte) {
        regs[reg] = value
    }

    fun readReg16(reg: Int): Int {
        val r1 = readRegInt(reg)
        val r2 = readRegInt(reg + 1)

        return ((r1 shl 8) + r2)
    }

    fun writeReg16(reg: Int, value: Int) {
        writeReg(reg, (value ushr 8).toByte())
        writeReg(reg + 1, (value).toByte())
    }

    fun setFlag(flag: Int) {
        var flagReg = readRegInt(REG_F)
        flagReg = flagReg or (1 shl flag);
        writeReg(REG_F, flagReg.toByte())
    }

    fun resetFlag(flag: Int) {
        var flagReg = readRegInt(REG_F)
        flagReg = flagReg and (1 shl flag).inv();
        writeReg(REG_F, flagReg.toByte())
    }

    fun toggleFlag(flag: Int) {
        var flagReg = readRegInt(REG_F)
        flagReg = flagReg xor (1 shl flag);
        writeReg(REG_F, flagReg.toByte())
    }

    fun isFlagSet(flag: Int): Boolean {
        var flagReg = readRegInt(REG_F)
        return flagReg and (1 shl flag) != 0
    }

    fun update() {
        var opcode = emulator.readInt(pc)

        var instr: Instr?
        if (opcode == 0xCB) {
            instr = emulator.cpu.extOp[opcode]
        } else {
            instr = emulator.cpu.op[opcode]
        }

        if (instr == null) throw EmulatorException("Illegal opcode: ${toHex(opcode.toByte())} at ${toHex(pc)}")

        if (instr is JmpInstr) {
            val result = instr.op.invoke()
            if (result == false) pc += instr.len;
        } else {
            instr.op.invoke();
            pc += instr.len
        }
    }
}

open class Instr(val len: Int,
                 val cycles: Int,
                 val name: String,
                 val op: () -> Any?) {
}

class VoidInstr(len: Int,
                cycles: Int,
                name: String,
                op: () -> Unit) : Instr(len, cycles, name, op)

class JmpInstr(len: Int,
               cycles: Int,
               name: String,
               op: () -> Boolean) : Instr(len, cycles, name, op) {

}
