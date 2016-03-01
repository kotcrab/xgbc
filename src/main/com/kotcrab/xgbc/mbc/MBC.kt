package com.kotcrab.xgbc.mbc

/** @author Kotcrab */
interface MBC {
    fun read(addr: Int): Byte

    fun write(addr: Int, value: Byte)
}
