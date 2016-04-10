package com.kotcrab.xgbc.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.file.FileUtils
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.EmulatorMode
import com.kotcrab.xgbc.changed
import com.kotcrab.xgbc.toHex

/** @author Kotcrab */

class DebuggerWindow(val emulator: Emulator) : VisWindow("Debugger") {
    init {
        defaults().left()

        val tabbedPane = TabbedPane()
        val tabbedPaneContent = VisTable()

        tabbedPane.addListener(object : TabbedPaneAdapter() {
            override fun switchedTab(tab: Tab?) {
                if (tab == null) return
                tabbedPaneContent.clearChildren()
                tabbedPaneContent.add(tab.contentTable).left().top().grow()
            }
        })

        tabbedPane.add(DebuggerTab(emulator))
        tabbedPane.add(MemoryTab(emulator))
        tabbedPane.add(CartridgeInfoTab(emulator))
        tabbedPane.switchTab(0)

        add(tabbedPane.table).growX().row()
        add(tabbedPaneContent).size(500.0f, 500.0f)

        pack()
        centerWindow()

        emulator.mode = EmulatorMode.DEBUGGING
    }
}

class DebuggerTab(val emulator: Emulator) : Tab(false, false) {
    private val table = VisTable(false)

    init {
        table.left().top()
        table.defaults().left()
        table.add(OpCodesDebuggerTab(emulator)).growX().row()
        table.add("CPU").padTop(10.0f).row()
        table.add(CpuDebuggerTable(emulator)).row()

        val stepButton = VisTextButton("Step")
        stepButton.changed { changeEvent, actor -> emulator.step() }

        table.add(stepButton)
    }

    override fun getContentTable(): Table? {
        return table
    }

    override fun getTabTitle(): String? {
        return "Debugger"
    }
}

class CartridgeInfoTab(val emulator: Emulator) : Tab(false, false) {
    private val table: VisTable = VisTable(false)

    init {
        table.left().top()
        table.defaults().left()
        table.add(VisLabel("Title: ${emulator.rom.title}")).row()
        table.add(VisLabel("GameBoy Color: ${emulator.rom.gameBoyColor}")).row()
        table.add(VisLabel("Super GameBoy: ${emulator.rom.superGameBoy}")).row()
        table.add(VisLabel("Cartridge Type: ${emulator.rom.cartridgeType}")).row()
        table.add(VisLabel("ROM Size: ${FileUtils.readableFileSize(emulator.rom.romSize.toLong())}")).row()
        table.add(VisLabel("RAM Size: ${FileUtils.readableFileSize(emulator.rom.ramSize.toLong())}")).row()
        table.add(VisLabel("Destination code: ${emulator.rom.destCode}")).row()
    }

    override fun getContentTable(): Table? {
        return table
    }

    override fun getTabTitle(): String? {
        return "Cartridge Info"
    }
}

class MemoryTab(val emulator: Emulator) : Tab(false, false) {
    private val table: VisTable = VisTable(false)
    private val scrollPaneContainer: VisTable = VisTable(false)
    private val scrollPane: VisScrollPane = VisScrollPane(table)

    init {
        scrollPaneContainer.add(scrollPane).grow()

        scrollPane.setFadeScrollBars(false)
        scrollPane.setFlickScroll(false)

        table.left().top()
        table.defaults().left()

        var first = true
        for (addr in 0x0000..0x100F) {
            val line = addr % 0x10 == 0
            if (line) {
                if (first == false) table.row()
                table.add(toHex(addr))
                first = false
            }

            table.add(toHex(emulator.read(addr))).width(20.0f)
        }
    }

    override fun getContentTable(): Table? {
        return scrollPaneContainer
    }

    override fun getTabTitle(): String? {
        return "Memory"
    }
}
