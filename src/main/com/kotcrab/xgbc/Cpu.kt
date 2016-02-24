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
    val extOp = arrayOfNulls<Instr>(256)

    init {
        fillOpCodes(op)
        fillExtOpCodes(extOp)
    }
}

open class Instr(val len: Int, val cycles: Int, val name: String, val op: (addr: Int) -> Any?)
class CondInstr(len: Int, cycles: Int, val cyclesIfActionNotTaken: Int, name: String, op: (addr: Int) -> Boolean)
: Instr(len, cycles, name, op)
