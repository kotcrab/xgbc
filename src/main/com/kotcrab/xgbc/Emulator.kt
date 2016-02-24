package com.kotcrab.xgbc

import com.badlogic.gdx.files.FileHandle

/** @author Kotcrab */
class Emulator(romFile: FileHandle) {
    val rom = Rom(romFile)
    val cpu = Cpu(this)

    fun read(addr: Int): Byte {
        if (addr < 0x8000)
            return rom.read(addr);

        throw EmulatorException("Unsupported address: " + addr);
    }

    fun readInt(addr: Int): Int {
        return read(addr).toInt() and 0xFF;
    }

    fun read16(addr: Int): Int {
        val ls = readInt(addr)
        val hs = readInt(addr + 1)

        return ((hs shl 8) + ls)
    }
}

fun toHex(addr: Int) = String.format("%04X", addr)
fun toHex(addr: Byte) = String.format("%02X", addr)
