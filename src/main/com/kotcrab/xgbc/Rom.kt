package com.kotcrab.xgbc

import com.badlogic.gdx.files.FileHandle

/** @author Kotcrab */
class Rom(romFile: FileHandle) {
    val rom: ByteArray = romFile.readBytes()

    val title: String by lazy {
        val builder = StringBuilder()
        for (addr in 0x0134..0x0142) {
            val byte = read(addr)
            if (byte.equals(0)) break
            builder.append(byte.toChar())
        }
        builder.toString()
    }

    val gameBoyColor: Boolean by lazy {
        read(0x143).equals(0x80)
    }

    val superGameBoy: Boolean by lazy {
        read(0x146).equals(0x03)
    }

    val cartridgeType: CartridgeType by lazy {
        cartridgeTypeFromByte(read(0x147))
    }

    val romSize: Int by lazy {
        var size = 0
        when (readInt(0x148)) {
            0x0 -> size = 32 * 1024
            0x1 -> size = 64 * 1024
            0x2 -> size = 128 * 1024
            0x3 -> size = 256 * 1024
            0x4 -> size = 512 * 1024
            0x5 -> size = 1024 * 1024
            0x6 -> size = 2048 * 1024
            0x52 -> size = 72 * 16 * 1024 //72 banks
            0x53 -> size = 80 * 16 * 1024
            0x54 -> size = 96 * 16 * 1024
            else -> throw EmulatorException("Unknown ROM size")
        }
        size
    }

    val ramSize: Int by lazy {
        var size = 0
        when (readInt(0x149)) {
            0x0 -> size = 0
            0x1 -> size = 2 * 1024
            0x2 -> size = 8 * 1024
            0x3 -> size = 32 * 1024
            0x4 -> size = 128 * 1024
            else -> throw EmulatorException("Unknown RAM size")
        }
        size
    }

    val destCode: Int by lazy {
        readInt(0x014A)
    }

    fun read(addr: Int): Byte {
        if (addr < 0x8000)
            return rom[addr]

        throw EmulatorException("Address out of range: " + addr)
    }

    fun readInt(addr: Int): Int {
        return read(addr).toInt() and 0xFF
    }

    fun write(addr: Int, value: Byte) {
        //throw EmulatorException("Illegal ROM write. MBC not implemented.")
    }
}
