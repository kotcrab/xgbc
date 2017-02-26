package com.kotcrab.xgbc.test

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.cpu.JmpInstr
import com.kotcrab.xgbc.toHex
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/** @author Kotcrab */
class TestConditionalOpCodesTimings {
    lateinit var emulator: Emulator

    @Before
    fun setUp() {
        emulator = Emulator(Lwjgl3Files().internal("test_rom.gb"))
    }

    @Test
    fun testConditionalTimings() {
        val instrTiming = arrayOf(
                1, 3, 2, 2, 1, 1, 2, 1, 5, 2, 2, 2, 1, 1, 2, 1,
                0, 3, 2, 2, 1, 1, 2, 1, 3, 2, 2, 2, 1, 1, 2, 1,
                3, 3, 2, 2, 1, 1, 2, 1, 3, 2, 2, 2, 1, 1, 2, 1,
                3, 3, 2, 2, 3, 3, 3, 1, 3, 2, 2, 2, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                2, 2, 2, 2, 2, 2, 0, 2, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                5, 3, 4, 4, 6, 4, 2, 4, 5, 4, 4, 0, 6, 6, 2, 4,
                5, 3, 4, 0, 6, 4, 2, 4, 5, 4, 4, 0, 6, 0, 2, 4,
                3, 3, 2, 0, 0, 4, 2, 4, 4, 1, 4, 0, 0, 0, 2, 4,
                3, 3, 2, 1, 0, 4, 2, 4, 3, 2, 4, 1, 0, 0, 2, 4)

        for (i in 0x00..0xFF - 1) {
            val instrCycles = instrTiming[i]
            val instr = emulator.cpu.op[i]
            if (instrCycles == 0 || instr == null)
                continue

            var cycles = instr.realCycles
            if (instr is JmpInstr) {
                cycles = instr.cyclesIfTaken + instr.internalCycles
            }

            Assert.assertEquals("Invalid op code conditional timing: OP: ${toHex(i)}, expected $instrCycles MC", cycles, instrCycles * 4)
        }
    }
}
