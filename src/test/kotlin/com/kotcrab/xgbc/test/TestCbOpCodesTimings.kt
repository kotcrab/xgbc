package com.kotcrab.xgbc.test

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.toHex
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/** @author Kotcrab */
class TestCbOpCodesTimings {
    lateinit var emulator: Emulator

    @Before
    fun setUp() {
        emulator = Emulator(Lwjgl3Files().internal("test_rom.gb"))
    }

    @Test
    fun testCbTimings() {
        val instrTiming = arrayOf(
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 3, 2,
                2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 3, 2,
                2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 3, 2,
                2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 3, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
                2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2)

        for (i in 0x00..0xFF - 1) {
            val instrCycles = instrTiming[i]
            val instr = emulator.cpu.extOp[i]
            if (instrCycles == 0 || instr == null)
                continue

            Assert.assertEquals("Invalid CB op code timing: OP: ${toHex(i)}, expected $instrCycles MC", instr.realCycles, instrCycles * 4)
        }
    }
}
