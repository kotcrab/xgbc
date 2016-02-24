package com.kotcrab.xgbc.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.vis.ui.widget.file.FileUtils
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter
import com.kotcrab.xgbc.Emulator
import com.kotcrab.xgbc.toHex

/** @author Kotcrab */

class DebuggerWindow(val emulator: Emulator) : VisWindow("Debugger") {
    init {
        defaults().left()

        val tabbedPane = TabbedPane()
        val tabbedPaneContent = VisTable()

        tabbedPane.addListener(object : TabbedPaneAdapter() {
            override fun switchedTab(tab: Tab?) {
                if (tab == null) return;
                tabbedPaneContent.clearChildren()
                tabbedPaneContent.add(tab.contentTable).left().top().grow()
            }
        })

        tabbedPane.add(OpcodesTab(emulator))
        tabbedPane.add(MemoryTab(emulator))
        tabbedPane.add(CartridgeInfoTab(emulator))
        tabbedPane.switchTab(0)

        add(tabbedPane.table).growX().row()
        add(tabbedPaneContent).size(500.0f, 300.0f)

        pack()
        centerWindow()
    }
}

class CartridgeInfoTab(val emulator: Emulator) : Tab(false, false) {
    private val table: VisTable = VisTable(false)

    init {
        table.left().top()
        table.defaults().left()
        table.add(VisLabel("Title: " + emulator.rom.title)).row()
        table.add(VisLabel("GameBoy Color: " + emulator.rom.gameBoyColor)).row()
        table.add(VisLabel("Super GameBoy: " + emulator.rom.superGameBoy)).row()
        table.add(VisLabel("Cartridge Type: " + emulator.rom.cartridgeType)).row()
        table.add(VisLabel("ROM Size: " + FileUtils.readableFileSize(emulator.rom.romSize.toLong()))).row()
        table.add(VisLabel("RAM Size: " + FileUtils.readableFileSize(emulator.rom.ramSize.toLong()))).row()
        table.add(VisLabel("Destination code: " + emulator.rom.destCode)).row()
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
            val line = addr % 0x10 == 0;
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

class OpcodesTab(val emulator: Emulator) : Tab(false, false) {
    private val table: VisTable = VisTable(false)
    private val scrollPaneContainer: VisTable = VisTable(false)
    private val scrollPane: VisScrollPane = VisScrollPane(table)

    init {
        scrollPaneContainer.add(scrollPane).grow()

        scrollPane.setFadeScrollBars(false)
        scrollPane.setFlickScroll(false)

        table.left().top()
        table.defaults().left()

        var addr = 0x0150;
        while (addr < 0x0300) {
            val opcode = emulator.read(addr)
            val instr = emulator.cpu.op[opcode.toInt() and 0xFF]
            if (instr == null) {
                table.add("Unsupported opcode: ${toHex(opcode)} at ${toHex(addr)}")
                addr += 1;
            } else {
                var evaluatedName = instr.name.replace("a16", toHex(emulator.read16(addr + 1)));
                evaluatedName = evaluatedName.replace("d16", toHex(emulator.read16(addr + 1)))

                if (evaluatedName.equals(instr.name))
                    table.add("${toHex(addr)}: ${instr.name}")
                else
                    table.add("${toHex(addr)}: $evaluatedName [${instr.name}]")
                addr += instr.len;
            }
            table.row()
        }
    }

    override fun getContentTable(): Table? {
        return scrollPaneContainer
    }

    override fun getTabTitle(): String? {
        return "Opcodes"
    }
}
