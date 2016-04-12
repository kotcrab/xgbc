package com.kotcrab.xgbc

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

fun Actor.changed(callback: (ChangeListener.ChangeEvent, Actor) -> Any?) {
    this.addListener(object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            callback.invoke(event!!, actor!!);
        }
    })
}

fun toHex(addr: Int) = String.format("%04X", addr)
fun toHex(addr: Byte) = String.format("%02X", addr)

fun Byte.toUnsignedInt(): Int = (this.toInt() and 0xFF)

fun Byte.isBitSet(bit: Int): Boolean {
    if (bit >= 8) throw IllegalArgumentException("Out of range, bit must be <8");
    return (this.toUnsignedInt()) and (1 shl bit) != 0
}

fun Byte.setBit(bit: Int): Byte {
    if (bit >= 8) throw IllegalArgumentException("Out of range, bit must be <8");
    return (this.toUnsignedInt() or (1 shl bit)).toByte()
}

fun Byte.resetBit(bit: Int): Byte {
    if (bit >= 8) throw IllegalArgumentException("Out of range, bit must be <8");
    return (this.toUnsignedInt() and (1 shl bit).inv()).toByte()
}

fun Byte.toggleBit(bit: Int): Byte {
    if (bit >= 8) throw IllegalArgumentException("Out of range, bit must be <8");
    return (this.toUnsignedInt() xor (1 shl bit)).toByte()
}

fun Byte.setBitState(bit: Int, bitState: Boolean): Byte {
    if ((bitState && isBitSet(bit) == false) || (bitState == false && isBitSet(bit))) {
        return toggleBit(bit)
    }

    return this
}

fun Byte.rotateRight(dist: Int): Byte = ((this.toUnsignedInt() ushr dist) or ((this.toUnsignedInt()) shl (8 - dist))).toByte()
fun Byte.rotateLeft(dist: Int): Byte = ((this.toUnsignedInt() shl dist) or ((this.toUnsignedInt()) ushr (8 - dist))).toByte()

fun Boolean.toInt(): Int = if (this) 1 else 0
