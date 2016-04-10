package com.kotcrab.xgbc.ui

import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.kotcrab.xgbc.changed

/** @author Kotcrab */
class DebuggerPopupMenu(val listener: Listener) : PopupMenu() {
    var ctxAddr = 0x0000;

    init {
        val runToHere = MenuItem("Run to here");
        runToHere.changed { changeEvent, actor -> listener.runToLine(ctxAddr) }
        addItem(runToHere)
    }

    interface Listener {
        fun runToLine(ctxAddr: Int)
    }
}
