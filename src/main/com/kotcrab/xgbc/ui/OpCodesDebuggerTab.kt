package com.kotcrab.xgbc.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.kotcrab.vis.ui.util.value.PrefHeightIfVisibleValue
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.xgbc.DebuggerListener
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.Instr

/** @author Kotcrab */
class OpCodesDebuggerTab(val emulator: Emulator) : VisTable(false), DebuggerPopupMenu.Listener {
    val tmpVector = Vector2()

    val chunkSize = 0xFF
    val chunksNumber = 0xFFFF / chunkSize;
    val chunks = arrayOfNulls<Chunk>(chunksNumber)

    private var currentChunkIndex = 0
    private var nextParseAddr = 0
    private var reparseInProgress: Boolean = false

    val chunksGroup = VerticalGroup()
    lateinit var scrollPane: VisScrollPane;

    private val debuggerPopupMenu = DebuggerPopupMenu(this)

    private var execStopAddr = -1

    init {
        left().top()
        defaults().left()

        chunksGroup.left()

        scrollPane = object : VisScrollPane(chunksGroup) {
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
                val currentLine = getOpCodeLine(pc)
                currentLine?.setCurrentLine(true)
                scrollTo(currentLine)

                if (execStopAddr == pc) {
                    execStopAddr = -1;
                }
            }

            override fun onMemoryWrite(addr: Int, value: Byte) {
                currentChunkIndex = addr / chunkSize;
                nextParseAddr = addr;
                reparseInProgress = true
            }
        })
    }

    private fun scrollTo(line: OpCodeLine?) {
        if (line == null) return

        tmpVector.set(0.0f, -100.0f)
        line.localToAscendantCoordinates(chunksGroup, tmpVector)
        scrollPane.scrollTo(tmpVector.x, tmpVector.y, line.width, line.height)
    }

    private fun getChunk(addr: Int): Chunk? {
        val chunkIndex = addr / chunkSize;
        val chunkOffset = addr % chunkSize;

        return chunks[chunkIndex];
    }

    private fun getOpCodeLine(addr: Int): OpCodeLine? {
        val chunkIndex = addr / chunkSize;
        val chunkOffset = addr % chunkSize;

        return chunks[chunkIndex]?.getOpCodeLine(chunkOffset)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)


        val cycles = Emulator.CLOCK * Gdx.graphics.deltaTime

        if (execStopAddr != -1) {
            while (true) {
                emulator.step()
                if (emulator.cpu.cycle > cycles) {
                    emulator.cpu.cycle = 0
                    break
                }

                if (execStopAddr == -1) {
                    break;
                }
            }
        }

        parseNextChunk()
        if(reparseInProgress) {
            reparseNextChunk()
        }
    }

    private fun parseNextChunk() {
        if (currentChunkIndex >= chunksNumber) return
        val chunk = Chunk(currentChunkIndex * chunkSize, nextParseAddr)
        nextParseAddr = chunk.parseEndAddr
        chunksGroup.addActor(chunk)
        chunks[currentChunkIndex] = chunk
        currentChunkIndex += 1
    }

    private fun reparseNextChunk() {
        if (currentChunkIndex >= chunksNumber) {
            reparseInProgress = false;
            return
        }
        val chunk = chunks[currentChunkIndex]!!
        chunk.update(nextParseAddr)
        nextParseAddr = chunk.parseEndAddr
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
                lines[i] = OpCodeLine(debuggerPopupMenu)
                lines[i]?.isVisible = false
                add(lines[i]).height(PrefHeightIfVisibleValue.INSTANCE).row()
            }

            update(beginParseFromAddr)
        }

        public fun update(beginParseFromAddr: Int) {
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

    override fun runToLine(ctxAddr: Int) {
        execStopAddr = ctxAddr
    }
}
