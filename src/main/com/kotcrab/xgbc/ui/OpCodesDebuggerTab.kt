package com.kotcrab.xgbc.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.value.PrefHeightIfVisibleValue
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.xgbc.*
import com.kotcrab.xgbc.vis.TableBuilder

/** @author Kotcrab */
class OpCodesDebuggerTab(val emulator: Emulator) : VisTable(false), DebuggerPopupMenu.Listener {
    val tmpVector = Vector2()

    val chunkSize = 1027
    val chunks = arrayOfNulls<Chunk>(0xFFFF / chunkSize + 1)
    var activeChunk: Chunk? = null;

    private var execStopAddr = -1

    val chunkContainer = VisTable()
    lateinit var scrollPane: VisScrollPane;
    val chunkInfoLabel = VisLabel()
    val chunkSelector = NumberSelector("Fragment", 0.0f, 0.0f, chunks.size - 1.toFloat(), 1.0f)
    val goToAddressField = VisValidatableTextField("")

    private val debuggerPopupMenu = DebuggerPopupMenu(this)

    init {
        left().top()
        defaults().left()

        chunkContainer.left()

        scrollPane = object : VisScrollPane(chunkContainer) {
            override fun getMouseWheelY(): Float {
                return 60.0f
            }

            override fun getPrefHeight(): Float {
                return chunkSize * 19.0f
            }
        }

        scrollPane.setFlickScroll(false)
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setFadeScrollBars(false)
        scrollPane.setOverscroll(false, false)
        scrollPane.setSmoothScrolling(false)
        add(scrollPane).growX().row()
        add(TableBuilder.build(VisUI.getSizes().spacingRight.toInt(), chunkSelector, chunkInfoLabel, VisLabel("Go to: "), goToAddressField))

        chunkSelector.setProgrammaticChangeEvents(false)
        chunkSelector.addChangeListener { index -> switchChunk(index.toInt()) }

        emulator.addDebuggerListener(object : DebuggerListener {
            override fun onCpuTick(oldPc: Int, pc: Int) {
                getOpCodeLine(oldPc)?.setCurrentLine(false)
                val targetChunk = getChunk(pc)!!
                if (targetChunk.uiReady == false) {
                    targetChunk.parse()
                }
                val currentLine = getOpCodeLine(pc)
                currentLine?.setCurrentLine(true)
                scrollTo(currentLine)

                if (execStopAddr == pc) {
                    execStopAddr = -1
                }
            }

            override fun onMemoryWrite(addr: Int, value: Byte) {
                if (execStopAddr == -1) {
                    reparseChunks();
                }
            }
        })

        goToAddressField.setFocusTraversal(false)
        goToAddressField.setProgrammaticChangeEvents(false)
        goToAddressField.textFieldFilter = VisTextField.TextFieldFilter { textField, c -> Character.isDigit(c) || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F' }
        goToAddressField.maxLength = 4
        goToAddressField.changed { changeEvent, actor ->
            if (goToAddressField.text.equals("") == false) {
                val addr = Integer.parseInt(goToAddressField.text, 16)
                scrollToAddr(addr)
            }
        }

        var nextParseAddr = 0;
        for (index in chunks.indices) {
            val chunk = Chunk(index * chunkSize, nextParseAddr)
            chunks[index] = chunk;
            nextParseAddr = chunk.parseEndAddr
        }
        switchChunk(0)
    }

    fun scrollToExecPoint() {
        scrollToAddr(emulator.cpu.pc)
    }

    private fun switchChunk(index: Int) {
        val chunk = chunks[index]!!
        if (chunk == activeChunk) return;
        reparseChunks()
        chunk.parse()
        activeChunk = chunk;
        chunkContainer.clearChildren()
        chunkContainer.add(chunk)
        scrollPane.validate()

        chunkInfoLabel.setText("Showing ${toHex(chunk.chunkBeginAddr)}-${toHex(Math.min(0xFFFF, chunk.chunkBeginAddr + chunkSize - 1))}")
        chunkSelector.value = index.toFloat();
    }

    private fun scrollToAddr(addr: Int) {
        val targetChunk = getChunk(addr)!!
        if (targetChunk.uiReady == false) {
            targetChunk.parse()
        }
        val currentLine = getOpCodeLine(addr)
        scrollTo(currentLine)
    }

    private fun scrollTo(line: OpCodeLine?) {
        if (line == null) return

        switchChunk(line.addr / chunkSize)
        tmpVector.set(0.0f, 0.0f)
        line.localToParentCoordinates(tmpVector)
        scrollPane.scrollTo(tmpVector.x, tmpVector.y, line.width, line.height, false, true)
    }

    private fun getChunk(addr: Int): Chunk? {
        val chunkIndex = addr / chunkSize;
        return chunks[chunkIndex];
    }

    private fun getOpCodeLine(addr: Int): OpCodeLine? {
        return getChunk(addr)?.getOpCodeLine(addr)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        if (Gdx.input.isKeyPressed(Input.Keys.F3)) {
            emulator.step()
        }

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
    }

    override fun runToLine(ctxAddr: Int) {
        execStopAddr = ctxAddr
    }

    private fun reparseChunks() {
        var nextParseAddr = 0;
        for (index in chunks.indices) {
            val chunk = chunks[index]!!;
            chunk.parse(nextParseAddr, chunk == activeChunk)
            nextParseAddr = chunk.parseEndAddr
        }
    }

    inner class Chunk(val chunkBeginAddr: Int, private var parseBeginAddr: Int) : VisTable(false) {
        var uiReady = false
            private set
        private val lines = arrayOfNulls<OpCodeLine>(chunkSize)

        var parseEndAddr: Int = 0;
            private set

        init {
            left().top()
            defaults().left()

            parse(parseBeginAddr, false)
        }

        fun parse() {
            parse(this.parseBeginAddr, true)
        }

        fun parse(parseBeginAddr: Int, updateUI: Boolean) {
            this.parseBeginAddr = parseBeginAddr;

            if (updateUI && uiReady == false) prepareUI()
            for (i in 0x00..chunkSize - 1) {
                lines[i]?.isVisible = false
            }

            var addr = parseBeginAddr;
            while (addr < chunkBeginAddr + chunkSize) {
                var opcode = emulator.read(addr)
                var opcodeInt = opcode.toUnsignedInt()

                var instr: Instr?
                if (opcodeInt == 0xCB) {
                    opcode = emulator.read(addr + 1)
                    opcodeInt = opcode.toUnsignedInt()
                    instr = emulator.cpu.extOp[opcodeInt]
                } else {
                    instr = emulator.cpu.op[opcodeInt]
                }

                if (updateUI) updateUI(emulator, addr, instr)

                if (instr != null) {
                    addr += instr.len
                } else {
                    addr += 1
                }

                if (addr > 0xFFFF) {
                    break
                }
            }
            parseEndAddr = addr
        }

        fun prepareUI() {
            for (i in 0x00..chunkSize - 1) {
                lines[i] = OpCodeLine(debuggerPopupMenu)
                add(lines[i]).height(PrefHeightIfVisibleValue.INSTANCE).row()
            }
            uiReady = true
        }

        private fun updateUI(emulator: Emulator, addr: Int, instr: Instr?) {
            lines[addr - chunkBeginAddr]?.parse(emulator, addr, instr)
            lines[addr - chunkBeginAddr]?.isVisible = true
        }

        fun getOpCodeLine(addr: Int): OpCodeLine? {
            if (uiReady == false) throw IllegalStateException("Chunk UI not ready")
            return lines[addr - chunkBeginAddr]
        }
    }
}
