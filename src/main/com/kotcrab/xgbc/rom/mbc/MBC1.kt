package com.kotcrab.xgbc.rom.mbc

import com.kotcrab.xgbc.EmulatorException
import com.kotcrab.xgbc.isBitSet
import com.kotcrab.xgbc.rom.Rom
import com.kotcrab.xgbc.toHex
import com.kotcrab.xgbc.toUnsignedInt

/** @author Kotcrab */
class MBC1(private val rom: Rom) : MBC {
    var mode = MBC1Mode.ROM16_RAM8;

    var romBankSelector = 0
    var activeRomBank = 1

    var ramEnabled = false
    val ramBanks: Array<ByteArray> = Array(4, { i -> ByteArray(0x2000) })
    var activeRamBank = ramBanks[0]

    override fun write(addr: Int, value: Byte) {
        if (addr in 0x0000..0x2000 - 1) {
            val ramWrite = value.toUnsignedInt() and 0xF
            ramEnabled = (ramWrite == 0x0A)
            return
        }

        if (addr in 0x2000..0x4000) {
            romBankSelector = romBankSelector and 0xE0
            romBankSelector = romBankSelector or (value.toUnsignedInt() and 0x1F)
            updateRomBank()
            return
        }

        if (addr in 0x4000..0x6000 - 1) {
            if (mode == MBC1Mode.ROM16_RAM8) {
                romBankSelector = romBankSelector and 0xCF
                romBankSelector = romBankSelector or (value.toUnsignedInt() shl 4)
                updateRomBank()
            } else {
                activeRamBank = ramBanks[value.toUnsignedInt() and 0x03];
            }
        }

        if (addr in 0x6000..0x8000 - 1) {
            if (value.isBitSet(0)) {
                mode = MBC1Mode.ROM16_RAM8
            } else {
                mode = MBC1Mode.ROM4_RAM32
            }
            return
        }

        if (addr in 0xA000..0xC000 - 1) {
            if (ramEnabled) {
                activeRamBank[addr - 0xA000] = value
            }
            return
        }

        throw EmulatorException("Unsupported MBC1 write: at ${toHex(addr)}, value: ${toHex(value)}")
    }

    private fun updateRomBank() {
        activeRomBank = romBankSelector
        if (activeRomBank == 0x00) activeRomBank = 0x01
        if (activeRomBank == 0x20) activeRomBank = 0x21
        if (activeRomBank == 0x40) activeRomBank = 0x41
        if (activeRomBank == 0x60) activeRomBank = 0x61
    }

    override fun read(addr: Int): Byte {
        if (addr in 0x0000..0x4000 - 1) return rom.read(addr)
        if (addr in 0x4000..0x8000 - 1) return rom.read((activeRomBank - 1) * 0x4000 + addr)

        if (addr in 0xA000..0xC000 - 1) {
            if (ramEnabled) {
                return activeRamBank[addr - 0xA000]
            } else {
                return 0
            }
        }

        throw EmulatorException("Read address out of supported MBC1 read range: ${toHex(addr)}")
    }

    enum class MBC1Mode {
        ROM16_RAM8,
        ROM4_RAM32
    }
}

