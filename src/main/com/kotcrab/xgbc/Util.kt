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
fun Byte.rotateRight(dist: Int): Byte = (((this.toUnsignedInt()) ushr  dist) or (this.toUnsignedInt()) shl  (8 - dist)).toByte()
fun Byte.rotateLeft(dist: Int): Byte = (((this.toUnsignedInt()) shl dist) or (this.toUnsignedInt()) ushr (8 - dist)).toByte()
fun Byte.isBitSet(bit: Int): Boolean = (this.toUnsignedInt()) and (1 shl bit) != 0
fun Byte.setBit(bit: Int): Byte = (this.toUnsignedInt() or (1 shl bit)).toByte()
fun Byte.resetBit(bit: Int): Byte = (this.toUnsignedInt() and (1 shl bit).inv()).toByte()
fun Int.rotateRight(dist: Int, size: Int): Byte = ((this ushr dist) or this shl (size - dist)).toByte()
fun Int.rotateLeft(dist: Int, size: Int): Byte = ((this shl dist) or this ushr (size - dist)).toByte()
fun Boolean.toInt(): Int = if (this) 1 else 0
