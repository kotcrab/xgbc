package com.kotcrab.xgbc.io

import com.kotcrab.xgbc.*
import java.util.*

/** @author Kotcrab */
class Joypad(private val emulator: Emulator) : IODevice {
    val P1 = 0xFF00

    val pressedKeys = EnumSet.noneOf(JoypadKey::class.java)!!

    override fun register(registrar: (Int) -> Unit) {
        registrar(P1)
    }

    override fun tick(cyclesElapsed: Int) {
    }

    override fun reset() {
        pressedKeys.clear()
    }

    override fun onRead(addr: Int) {
    }

    override fun onWrite(addr: Int, value: Byte) {
        if (addr == P1) updateMemoryData()
    }

    private fun updateMemoryData() {
        val p1Value = emulator.read(P1)
        var newValue = (p1Value.toUnsignedInt() and 0b00110000 or 0b11001111).toByte()
        for (key in pressedKeys) {
            if (newValue.isBitSet(key.selectBit) == false) {
                newValue = newValue.resetBit(key.inBit)
            }
        }
        emulator.io.directWrite(P1, newValue)
    }

    fun keyPressed(key: JoypadKey) {
        pressedKeys.add(key)
        updateMemoryData()
        if (emulator.read(P1).isBitSet(key.selectBit)) {
            emulator.interrupt(Interrupt.JOYPAD)
        }
    }

    fun keyReleased(key: JoypadKey) {
        val removed = pressedKeys.remove(key)
        if (removed == false) return
        updateMemoryData()
    }

    enum class JoypadKey(val selectBit: Int, val inBit: Int) {
        LEFT(4, 1), RIGHT(4, 0), UP(4, 2), DOWN(4, 3),
        A(5, 0), B(5, 1), START(5, 3), SELECT(5, 2)
    }
}
