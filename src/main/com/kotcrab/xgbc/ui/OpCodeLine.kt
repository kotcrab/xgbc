package com.kotcrab.xgbc.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.xgbc.*

/** @author Kotcrab */
class OpCodeLine() : VisTable(false) {
    private val icon = VisImage()
    private val label = VisLabel("", "small")

    private var breakpoint = false
    private var currentLine = false

    init {
        add(icon).size(16.0f, 16.0f).padRight(2.0f)
        add(label)

        icon.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                setBreakpoint(!breakpoint)
            }
        })
    }

    fun parse(emulator: Emulator, addr: Int, instr: Instr?) {
        if (instr == null) {
            label.setText("Unsupported opcode at ${toHex(addr)}")
            return
        }

        var evaluatedName = instr.name;
        evaluatedName = evaluatedName.replace("d8", toHex(emulator.read(addr + 1)))
        evaluatedName = evaluatedName.replace("a8", toHex(emulator.read(addr + 1)))
        evaluatedName = evaluatedName.replace("r8", toHex(emulator.read(addr + 1)))
        if(addr < 0xFFFF - 1) {
            evaluatedName = evaluatedName.replace("a16", toHex(emulator.read16(addr + 1)))
            evaluatedName = evaluatedName.replace("d16", toHex(emulator.read16(addr + 1)))
        }

        if (evaluatedName.equals(instr.name))
            label.setText("${toHex(addr)}: ${instr.name}")
        else
            label.setText("${toHex(addr)}: $evaluatedName [${instr.name}]")
    }

    fun setBreakpoint(breakpoint: Boolean) {
        this.breakpoint = breakpoint;
        updateIcon()
    }

    fun setCurrentLine(currentLine: Boolean) {
        this.currentLine = currentLine;
        updateIcon()
    }

    private fun updateIcon() {
        if (currentLine)
            setIcon(Icon.CURRENT_LINE)
        else if (breakpoint)
            setIcon(Icon.BREAKPOINT)
        else
            icon.drawable = null
    }

    private fun setIcon(iconType: Icon) {
        icon.drawable = Assets.get(iconType)
    }
}
