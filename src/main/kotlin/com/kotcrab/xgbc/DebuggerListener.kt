package com.kotcrab.xgbc

import com.kotcrab.xgbc.cpu.Reg

/** @author Kotcrab */
interface DebuggerListener {
    fun onRegisterWrite(reg: Reg, value: Byte) {

    }

    fun onMemoryWrite(addr: Int, value: Byte) {

    }

    fun onCpuTick(oldPc: Int, pc: Int) {

    }
}
