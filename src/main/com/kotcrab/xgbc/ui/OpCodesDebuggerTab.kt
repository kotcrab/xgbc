package com.kotcrab.xgbc.ui

import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.xgbc.DebuggerListener
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.Instr
import com.kotcrab.xgbc.toHex

/** @author Kotcrab */
class OpCodesDebuggerTab(val emulator: Emulator) : VisTable(false) {
    val lines = arrayOfNulls<OpCodeLine>(0xFFFF)

    init {
        left().top()
        defaults().left()

        var addr = emulator.cpu.pc
        while (addr < emulator.cpu.pc + 10) {
            var opcode = emulator.read(addr)
            var opcodeInt = opcode.toInt() and 0xFF

            var instr: Instr?
            if (opcodeInt == 0xCB) {
                opcode = emulator.read(addr + 1)
                opcodeInt = opcode.toInt() and 0xFF
                instr = emulator.cpu.extOp[opcodeInt]
            } else {
                instr = emulator.cpu.op[opcodeInt]
            }

            if (instr == null) {
                add("Unsupported opcode: ${toHex(opcode)} at ${toHex(addr)}")
                addr += 1
            } else {
                lines[addr] = OpCodeLine(emulator, addr, instr)
                if (addr == emulator.cpu.pc) lines[addr]?.setCurrentLine(true)
                add(lines[addr])
                addr += instr.len
            }
            row()
        }

        emulator.addDebuggerListener(object : DebuggerListener{
            override fun onCpuTick(oldPc: Int, pc: Int) {
                lines[oldPc]?.setCurrentLine(false)
                lines[pc]?.setCurrentLine(true)
            }
        })
    }
}
