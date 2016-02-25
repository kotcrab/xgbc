package com.kotcrab.xgbc

/** @author Kotcrab */
class IO() {
    val io: ByteArray = ByteArray(0x4C)

    fun reset() {
        io.fill(0)
    }

    fun read(addr: Int): Byte {
        return io[addr]
    }

    fun write(addr: Int, value: Byte) {
        io[addr] = value
    }
}
