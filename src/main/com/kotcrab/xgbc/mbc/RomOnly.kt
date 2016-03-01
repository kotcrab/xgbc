package com.kotcrab.xgbc.mbc

import com.kotcrab.xgbc.EmulatorException
import com.kotcrab.xgbc.Rom

/** @author Kotcrab */
class RomOnly(private val rom: Rom) : MBC {
    override fun write(addr: Int, value: Byte) {
        throw EmulatorException("Illegal ROM write. This cartridge types does not use MBC")
    }

    override fun read(addr: Int): Byte {
        if (addr in 0xA000..0xC000 - 1) return 0

        return rom.read(addr)
    }
}
