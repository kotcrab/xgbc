package com.kotcrab.xgbc.mbc

import com.kotcrab.xgbc.EmulatorException
import com.kotcrab.xgbc.Rom
import com.kotcrab.xgbc.isBitSet
import com.kotcrab.xgbc.toHex

/** @author Kotcrab */
class MBC1(private val rom: Rom) : MBC {
    var activeRomBank = 1
    var activeRamBank = 0

    var mode = MBC1Mode.ROM16_RAM8;

    override fun write(addr: Int, value: Byte) {
        println("mbc write ${toHex(addr)}")
        if (addr in 0x6000..0x7FFF) {
            if (value.isBitSet(0)) {
                mode = MBC1Mode.ROM16_RAM8
            } else
                mode = MBC1Mode.ROM4_RAM32
        }
    }

    override fun read(addr: Int): Byte {
        if (addr in 0x0000..0x4000 - 1)
            return rom.read(addr)
        if (addr in 0x4000..0x8000 - 1)
            return rom.read((activeRomBank - 1) * 0x4000 + addr)

        if (addr in 0xA000..0xC000 - 1) {
            return 0
        }

        throw EmulatorException("Read address out of supported MBC1 read range: ${toHex(addr)}")
    }

    enum class MBC1Mode {
        ROM16_RAM8,
        ROM4_RAM32
    }
}

