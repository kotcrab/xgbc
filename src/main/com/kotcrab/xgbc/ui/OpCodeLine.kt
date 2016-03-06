package com.kotcrab.xgbc.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.xgbc.*

/** @author Kotcrab */
class OpCodeLine(emulator: Emulator, addr: Int, instr: Instr) : VisTable(false) {
    private lateinit var iconCell: Cell<VisImage>

    private var breakpoint = false
    private var currentLine = false

    init {
        iconCell = add(VisImage()).size(16.0f, 16.0f).padRight(2.0f)

        var evaluatedName = instr.name.replace("d16", toHex(emulator.read16(addr + 1)))
        evaluatedName = evaluatedName.replace("a16", toHex(emulator.read16(addr + 1)))
        evaluatedName = evaluatedName.replace("d8", toHex(emulator.read(addr + 1)))
        evaluatedName = evaluatedName.replace("a8", toHex(emulator.read(addr + 1)))
        evaluatedName = evaluatedName.replace("r8", toHex(emulator.read(addr + 1)))

        if (evaluatedName.equals(instr.name))
            add(VisLabel("${toHex(addr)}: ${instr.name}", "small"))
        else
            add(VisLabel("${toHex(addr)}: $evaluatedName [${instr.name}]", "small"))

        iconCell.actor.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                setBreakpoint(!breakpoint)
            }
        })
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
            iconCell.actor.drawable = null
    }

    private fun setIcon(icon: Icon) {
        iconCell.actor.drawable = Assets.get(icon)
    }
}
