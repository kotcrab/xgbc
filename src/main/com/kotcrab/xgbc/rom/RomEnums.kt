package com.kotcrab.xgbc.rom

import com.kotcrab.xgbc.EmulatorException

/** @author Kotcrab */
enum class CartridgeType(val type: Byte) {
    ROM(0x0),
    ROM_MBC1(0x1),
    ROM_MBC1_RAM(0x2),
    ROM_MBC1_RAM_BATT(0x3),
    ROM_MBC2(0x5),
    ROM_MBC2_BATT(0x6),
    ROM_RAM(0x8),
    ROM_RAM_BATT(0x9),
    ROM_MMMO1(0xB),
    ROM_MMMO1_SRAM(0xC),
    ROM_MMMO1_SRAM_BATT(0xD),
    ROM_MBC3_TIMER_BATT(0xF),
    ROM_MBC3_TIMER_RAM_BATT(0x10),
    ROM_MBC3(0x11),
    ROM_MBC3_RAM(0x12),
    ROM_MBC3_RAM_BATT(0x13),
    ROM_MBC5(0x19),
    ROM_MBC5_RAM(0x1A),
    ROM_MBC5_RAM_BATT(0x1B),
    ROM_MBC5_RUMBLE(0x1C),
    ROM_MBC5_RUMBLE_SRAM(0x1D),
    ROM_MBC5_RUMBLE_SRAM_BATT(0x1E),
    POCKET_CAMERA(0x1F),
    BANDAI_TAMA5(0xFD.toByte()),
    HUDSON_HUC3(0xFE.toByte()),
    HUDSON_HUC1(0xFF.toByte())
}

fun cartridgeTypeFromByte(type: Byte): CartridgeType {
    for (enumType in CartridgeType.values()) {
        if (enumType.type == type) return enumType
    }

    throw EmulatorException("Unsupported cartridge type: " + type)
}
