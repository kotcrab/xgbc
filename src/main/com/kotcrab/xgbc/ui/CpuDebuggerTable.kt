package com.kotcrab.xgbc.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.xgbc.Cpu
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.toHex
import com.kotcrab.xgbc.vis.TableBuilder

/** @author Kotcrab */
class CpuDebuggerTable(private val emulator: Emulator) : VisTable(true) {
    private val afLabel = VisLabel()
    private val bcLabel = VisLabel()
    private val deLabel = VisLabel()
    private val hlLabel = VisLabel()

    private val pcLabel = VisLabel()
    private val spLabel = VisLabel()

    private val zFlagCheck = VisCheckBox("Z")
    private val nFlagCheck = VisCheckBox("N")
    private val hFlagCheck = VisCheckBox("H")
    private val cFlagCheck = VisCheckBox("C")

    init {
        left().top()
        defaults().left()

        val regsTable = VisTable(false)
        regsTable.add("AF:")
        regsTable.add(afLabel)

        regsTable.row()
        regsTable.add("BC:")
        regsTable.add(bcLabel)

        regsTable.row()
        regsTable.add("DE:")
        regsTable.add(deLabel)

        regsTable.row()
        regsTable.add("HL:")
        regsTable.add(hlLabel)

        val miscTable = VisTable(false)
        miscTable.defaults().left()

        miscTable.add(TableBuilder.build(VisLabel("PC:"), pcLabel)).row()
        miscTable.add(TableBuilder.build(VisLabel("SP:"), spLabel)).row()
        miscTable.add(TableBuilder.build(zFlagCheck, nFlagCheck)).row()
        miscTable.add(TableBuilder.build(cFlagCheck, hFlagCheck)).row()

        add(regsTable)
        add(miscTable)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        afLabel.setText(toHex(emulator.cpu.readReg16(Cpu.REG_AF)))
        bcLabel.setText(toHex(emulator.cpu.readReg16(Cpu.REG_BC)))
        deLabel.setText(toHex(emulator.cpu.readReg16(Cpu.REG_DE)))
        hlLabel.setText(toHex(emulator.cpu.readReg16(Cpu.REG_HL)))

        pcLabel.setText(toHex(emulator.cpu.pc))
        spLabel.setText(toHex(emulator.cpu.sp))

        zFlagCheck.isChecked = emulator.cpu.isFlagSet(Cpu.FLAG_Z)
        nFlagCheck.isChecked = emulator.cpu.isFlagSet(Cpu.FLAG_N)
        hFlagCheck.isChecked = emulator.cpu.isFlagSet(Cpu.FLAG_H)
        cFlagCheck.isChecked = emulator.cpu.isFlagSet(Cpu.FLAG_C)
    }
}
