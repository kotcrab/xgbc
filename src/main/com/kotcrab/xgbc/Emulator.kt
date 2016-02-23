package com.kotcrab.xgbc

import com.badlogic.gdx.files.FileHandle

/** @author Kotcrab */
class Emulator(romFile: FileHandle) {
    val rom = Rom(romFile)
}
