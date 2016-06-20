package com.kotcrab.xgbc.rom.mbc

/** @author Kotcrab */
interface MBC {
    fun read(addr: Int): Byte

    fun write(addr: Int, value: Byte)
}
