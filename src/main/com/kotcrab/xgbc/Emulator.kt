package com.kotcrab.xgbc

import com.badlogic.gdx.files.FileHandle

/** @author Kotcrab */
class Emulator(romFile: FileHandle) {
    val rom = Rom(romFile)
    val cpu = Cpu(this)

    val ram: ByteArray = ByteArray(0x2000)
    val vram: ByteArray = ByteArray(0x2000)
    val oam: ByteArray = ByteArray(0x80)
    val internalRam: ByteArray = ByteArray(0xA0)

    /** Interrupt Enable */
    var ie: Byte = 0

    fun read(addr: Int): Byte {
        when (addr) {
            in 0x0000..0x8000 - 1 -> return rom.read(addr);
            in 0x8000..0xA000 - 1 -> return vram[addr - 0x8000];
            in 0xA000..0xC000 - 1 -> throw EmulatorException("Switchable RAM bank not implemented. Address: " + addr);
            in 0xC000..0xE000 - 1 -> return ram[addr - 0xC000];
            in 0xE000..0xFE00 - 1 -> return ram[addr - 0xE000]; //ram echo
            in 0xFE00..0xFEA0 - 1 -> return oam[addr - 0xFE00];
            in 0xFEA0..0xFF80 - 1 -> throw EmulatorException("IO not implemented. Address: " + addr);
            in 0xFE80..0xFFFF - 1 -> return internalRam[addr - 0xFE80]
            0xFFFF -> return ie;
            else -> throw EmulatorException("Unsupported address: " + addr)
        }
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
