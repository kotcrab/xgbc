package com.kotcrab.xgbc

/** @author Kotcrab */
interface DebuggerListener {
    fun onRegisterWrite(reg: Int, value: Byte) {

    }

    fun onMemoryWrite(addr: Int, value: Byte) {

    }

    fun onCpuTick(oldPc: Int, pc: Int) {

    }
}
