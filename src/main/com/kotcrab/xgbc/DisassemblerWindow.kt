package com.kotcrab.xgbc

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.vis.ui.widget.file.FileUtils

/** @author Kotcrab */

class DisassemblerWindow(val emulator: Emulator) : VisWindow("Emulator") {
    init {
        defaults().left()

        add(VisLabel("Title: " + emulator.rom.title)).row()
        add(VisLabel("GameBoy Color: " + emulator.rom.gameBoyColor)).row()
        add(VisLabel("Super GameBoy: " + emulator.rom.superGameBoy)).row()
        add(VisLabel("Cartridge Type: " + emulator.rom.cartridgeType)).row()
        add(VisLabel("ROM Size: " + FileUtils.readableFileSize(emulator.rom.romSize.toLong()))).row()
        add(VisLabel("RAM Size: " + FileUtils.readableFileSize(emulator.rom.ramSize.toLong()))).row()
        pack()
        centerWindow()
    }
}
