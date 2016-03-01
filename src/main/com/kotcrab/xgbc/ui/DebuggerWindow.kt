package com.kotcrab.xgbc.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.vis.ui.widget.file.FileUtils
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter
import com.kotcrab.xgbc.*

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

        tabbedPane.add(CpuTab(emulator))
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

class CpuTab(val emulator: Emulator) : Tab(false, false) {
    private val table: CpuTabTable = CpuTabTable()

    private val pcLabel: VisLabel = VisLabel()
    private val spLabel: VisLabel = VisLabel()

    private val afLabel: VisLabel = VisLabel()
    private val bcLabel: VisLabel = VisLabel()
    private val deLabel: VisLabel = VisLabel()
    private val hlLabel: VisLabel = VisLabel()

    private val flagsLabel: VisLabel = VisLabel()

    init {
        table.left().top()
        table.defaults().left()
        table.add("PC: ")
        table.add(pcLabel)

        table.row()
        table.add("SP: ")
        table.add(spLabel)

        table.row()
        table.add("AF: ")
        table.add(afLabel)

        table.row()
        table.add("BC: ")
        table.add(bcLabel)

        table.row()
        table.add("DE: ")
        table.add(deLabel)

        table.row()
        table.add("HL: ")
        table.add(hlLabel)

        table.row()
        table.add("Flags: ")
        table.add(flagsLabel)
    }

    override fun getContentTable(): Table? {
        return table
    }

    override fun getTabTitle(): String? {
        return "CPU"
    }

    inner  class CpuTabTable() : VisTable(false) {
        override fun draw(batch: Batch?, parentAlpha: Float) {
            super.draw(batch, parentAlpha)
            pcLabel.setText(toHex(emulator.cpu.pc))
            spLabel.setText(toHex(emulator.cpu.sp))
            afLabel.setText(toHex(emulator.cpu.readReg16(Cpu.REG_AF)))
            bcLabel.setText(toHex(emulator.cpu.readReg16(Cpu.REG_BC)))
            deLabel.setText(toHex(emulator.cpu.readReg16(Cpu.REG_DE)))
            hlLabel.setText(toHex(emulator.cpu.readReg16(Cpu.REG_HL)))
            flagsLabel.setText("Z ${emulator.cpu.isFlagSet(Cpu.FLAG_Z).toInt()} " +
                    "N ${emulator.cpu.isFlagSet(Cpu.FLAG_N).toInt()} " +
                    "H ${emulator.cpu.isFlagSet(Cpu.FLAG_H).toInt()} " +
                    "C ${emulator.cpu.isFlagSet(Cpu.FLAG_C).toInt()}")
        }
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

//        var addr = 0x0150
        var addr = 0x3E97
        while (addr < 0x47E0) {
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

            if (instr == null) {
                table.add("Unsupported opcode: ${toHex(opcode)} at ${toHex(addr)}")
                addr += 1
            } else {
                var evaluatedName = instr.name.replace("d16", toHex(emulator.read16(addr + 1)))
                evaluatedName = evaluatedName.replace("a16", toHex(emulator.read16(addr + 1)))
                evaluatedName = evaluatedName.replace("d8", toHex(emulator.read(addr + 1)))
                evaluatedName = evaluatedName.replace("a8", toHex(emulator.read(addr + 1)))
                evaluatedName = evaluatedName.replace("r8", toHex(emulator.read(addr + 1)))

                if (evaluatedName.equals(instr.name))
                    table.add("${toHex(addr)}: ${instr.name}")
                else
                    table.add("${toHex(addr)}: $evaluatedName [${instr.name}]")
                addr += instr.len
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
