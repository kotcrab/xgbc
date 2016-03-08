package com.kotcrab.xgbc.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.kotcrab.vis.ui.util.value.PrefHeightIfVisibleValue
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.xgbc.DebuggerListener
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.Instr

/** @author Kotcrab */
class OpCodesDebuggerTab(val emulator: Emulator) : VisTable(false) {
    val chunkSize = 0xFF
    val chunksNumber = 0xFFFF / chunkSize;
    val chunks = arrayOfNulls<Chunk>(chunksNumber)

    var currentChunkIndex = 0
    var nextParseAddr = 0

    val chunksGroup = VerticalGroup()

    init {
        left().top()
        defaults().left()

        chunksGroup.left()

        val scrollPane = object : VisScrollPane(chunksGroup) {
            override fun getMouseWheelY(): Float {
                return 150.0f
            }

            override fun getPrefHeight(): Float {
                return 0xFFFF * 19.0f
            }
        }
        scrollPane.setFlickScroll(false)
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setFadeScrollBars(false)
        scrollPane.setOverscroll(false, false)
        scrollPane.setSmoothScrolling(false)
        add(scrollPane).growX()

        emulator.addDebuggerListener(object : DebuggerListener {
            override fun onCpuTick(oldPc: Int, pc: Int) {
                getOpCodeLine(oldPc)?.setCurrentLine(false)
                getOpCodeLine(pc)?.setCurrentLine(true)
            }
        })
    }

    private fun getOpCodeLine(addr: Int): OpCodeLine? {
        val chunkIndex = addr / chunkSize;
        val chunkOffset = addr % chunkSize;

        return chunks[chunkIndex]?.getOpCodeLine(chunkOffset)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        parseNextChunk()
    }

    private fun parseNextChunk() {
        if (currentChunkIndex >= chunksNumber) return
        val chunk = Chunk(currentChunkIndex * chunkSize, nextParseAddr)
        nextParseAddr = chunk.parseEndAddr
        chunksGroup.addActor(chunk)
        chunks[currentChunkIndex] = chunk
        currentChunkIndex += 1
    }

    inner class Chunk(private val chunkBeginAddr: Int, beginParseFromAddr: Int) : VisTable(false) {
        val lines = arrayOfNulls<OpCodeLine>(chunkSize)

        var parseEndAddr: Int = 0;
            private set

        init {
            left().top()
            defaults().left()

            for (i in 0x00..chunkSize - 1) {
                lines[i] = OpCodeLine()
                lines[i]?.isVisible = false
                add(lines[i]).height(PrefHeightIfVisibleValue.INSTANCE).row()
            }

            update(beginParseFromAddr)
        }

        private fun update(beginParseFromAddr: Int) {
            var addr = beginParseFromAddr;
            while (addr < chunkBeginAddr + chunkSize) {
                var opcode = emulator.read(addr)
                var opcodeInt = opcode.toInt() and 0xFF

                var instr: Instr?
                if (opcodeInt == 0xCB) {
                    opcode = emulator.read(addr + 1)
                    opcodeInt = opcode.toInt() and 0xFF
                    instr = emulator.cpu.extOp[opcodeInt]
                } else {
                    instr = emulator.cpu.op[opcodeInt]
                }

                lines[addr - chunkBeginAddr]?.parse(emulator, addr, instr)
                lines[addr - chunkBeginAddr]?.isVisible = true

                if (instr != null)
                    addr += instr.len
                else
                    addr += 1
            }
            parseEndAddr = addr
        }

        fun getOpCodeLine(relAddr: Int): OpCodeLine? {
            return lines[relAddr]
        }
    }
}
