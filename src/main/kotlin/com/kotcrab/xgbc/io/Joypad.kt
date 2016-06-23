package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.*
import java.util.*

/** @author Kotcrab */
class Joypad(private val emulator: Emulator) : IODevice {
    val P1 = 0xFF00

    val pressedKeys = EnumSet.noneOf(JoypadKey::class.java)

    override fun register(registrar: (Int) -> Unit) {
        registrar.invoke(P1)
    }

    override fun tick(cyclesElapsed: Int) {
    }

    override fun reset() {
        pressedKeys.clear()
    }

    override fun onRead(addr: Int) {
    }

    override fun onWrite(addr: Int, value: Byte) {
        if(addr == P1) updateMemoryData(value)
    }

    private fun updateMemoryData(p1Value: Byte) {
        var newValue = (p1Value.toUnsignedInt() and 0b00110000 or 0b11001111).toByte()
        for (key in pressedKeys) {
            if (p1Value.isBitSet(key.outBit)) {
                newValue = newValue.resetBit(key.inBit)
            }
        }
        emulator.io.directWrite(P1, newValue)
    }

    fun keyPressed(key: JoypadKey) {
        pressedKeys.add(key)
    }

    fun keyReleased(key: JoypadKey) {
        val removed = pressedKeys.remove(key)
        if (removed == false) return
        val p1 = emulator.read(P1)
        updateMemoryData(p1)
        if (p1.isBitSet(key.outBit)) {
            emulator.interrupt(Interrupt.JOYPAD)
        }
    }

    enum class JoypadKey(val outBit: Int, val inBit: Int) {
        LEFT(4, 1), RIGHT(4, 0), UP(4, 2), DOWN(4, 3),
        A(5, 0), B(5, 1), START(5, 2), SELECT(5, 3)
    }
}
