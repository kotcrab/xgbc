package com.kotcrab.xgbc.ui

import com.kotcrab.vis.ui.util.value.PrefHeightIfVisibleValue
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.xgbc.DebuggerListener
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.toHex

/** @author Kotcrab */
class StackPointerView(val emulator: Emulator, opCodesDebuggerTab: OpCodesDebuggerTab) : VisTable() {
    private val numValuesShown = 7; //must be odd
    private val spOffset = (numValuesShown - 1)
    private val labels = arrayOfNulls<VisLabel>(numValuesShown)

    private var currentSp = -1;

    init {
        left().top()
        defaults().left()

        add("Stack Pointer", "small").row()
        for (i in labels.indices) {
            labels[i] = VisLabel("", "small")
            add(labels[i]).height(PrefHeightIfVisibleValue.INSTANCE).row()
        }

        emulator.addDebuggerListener(object : DebuggerListener {
            override fun onCpuTick(oldPc: Int, pc: Int) {
                if (opCodesDebuggerTab.execStopAddr != -1) return
                if (currentSp != emulator.cpu.sp) {
                    currentSp = emulator.cpu.sp
                    updateLabels()
                }
            }
        })
    }

    private fun updateLabels() {
        val sp = emulator.cpu.sp
        var index = 0
        for (addr in sp + spOffset downTo sp - spOffset step 2) {
            val label = labels[index]!!
            if (addr < 0xFFFF && addr > 0x0000) {
                label.setText(toHex(addr) + ": " + toHex(emulator.read16(addr)))
                label.isVisible = true
            } else {
                label.setText("")
                label.isVisible = false
            }
            index++
        }
    }
}
