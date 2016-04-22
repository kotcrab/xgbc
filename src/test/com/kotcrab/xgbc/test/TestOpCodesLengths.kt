package com.kotcrab.xgbc.test

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.toHex
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/** @author Kotcrab */
class TestOpCodesLengths {
    lateinit var emulator: Emulator;

    @Before
    fun setUp() {
        emulator = Emulator(Lwjgl3Files().internal("test_rom.gb"))
    }

    @Test
    fun testLengths() {
        val instrLength = arrayOf(
                1, 3, 1, 1, 1, 1, 2, 1, 3, 1, 1, 1, 1, 1, 2, 1,
                0, 3, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1,
                2, 3, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1,
                2, 3, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 3, 3, 3, 1, 2, 1, 1, 1, 3, 0, 3, 3, 2, 1,
                1, 1, 3, 0, 3, 1, 2, 1, 1, 1, 3, 0, 3, 0, 2, 1,
                2, 1, 1, 0, 0, 1, 2, 1, 2, 1, 3, 0, 0, 0, 2, 1,
                2, 1, 1, 1, 0, 1, 2, 1, 2, 1, 3, 1, 0, 0, 2, 1)

        for (i in 0x00..0xFF - 1) {
            val length = instrLength[i];
            val instr = emulator.cpu.op[i];
            if (length == 0 || instr == null)
                continue

            Assert.assertEquals("Invalid op code length: OP: ${toHex(i)}, expected $length", instr.len, length)
        }
    }
}
