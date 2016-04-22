package com.kotcrab.xgbc.test

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.toHex
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/** @author Kotcrab */
class TestOpCodesTimings {
    lateinit var emulator: Emulator;

    @Before
    fun setUp() {
        emulator = Emulator(Lwjgl3Files().internal("test_rom.gb"))
    }

    @Test
    fun testTimings() {
        val instrTiming = arrayOf(
                1, 3, 2, 2, 1, 1, 2, 1, 5, 2, 2, 2, 1, 1, 2, 1,
                0, 3, 2, 2, 1, 1, 2, 1, 3, 2, 2, 2, 1, 1, 2, 1,
                2, 3, 2, 2, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 2, 1,
                2, 3, 2, 2, 3, 3, 3, 1, 2, 2, 2, 2, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                2, 2, 2, 2, 2, 2, 0, 2, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                2, 3, 3, 4, 3, 4, 2, 4, 2, 4, 3, 0, 3, 6, 2, 4,
                2, 3, 3, 0, 3, 4, 2, 4, 2, 4, 3, 0, 3, 0, 2, 4,
                3, 3, 2, 0, 0, 4, 2, 4, 4, 1, 4, 0, 0, 0, 2, 4,
                3, 3, 2, 1, 0, 4, 2, 4, 3, 2, 4, 1, 0, 0, 2, 4)

        for (i in 0x00..0xFF - 1) {
            val instrCycles = instrTiming[i];
            val instr = emulator.cpu.op[i];
            if (instrCycles == 0 || instr == null)
                continue

            Assert.assertEquals("Invalid op code timing: OP: ${toHex(i)}, expected $instrCycles MC", instr.cycles, instrCycles * 4)
        }
    }
}
