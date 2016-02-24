package com.kotcrab.xgbc

/** @author Kotcrab */
class Cpu(private val emulator: Emulator) {
    var sp: Int = 0 //stack pointer
    var pc: Int = 0 //program counter

    var a: Byte = 0 //accumulator
    var f: Byte = 0 //flags

    var b: Byte = 0
    var c: Byte = 0

    var d: Byte = 0
    var e: Byte = 0

    var h: Byte = 0
    var l: Byte = 0

    val op = arrayOfNulls<Instr>(256)

    init {
        op[0x00] = Instr(1, 4, "NOP", {})
        op[0x01] = Instr(3, 12, "LD BC, d16", {})
        op[0x02] = Instr(1, 8, "LD (BC), A", {})
        op[0xC3] = Instr(3, 16, "JP a16", {})
    }
}

class Instr(val len: Int, val cycles: Int, val name: String, val op: () -> Any?)
