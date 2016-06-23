package com.kotcrab.xgbc.gdx

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.kotcrab.xgbc.io.Joypad

/** @author Kotcrab */
class GdxJoypad(private val joypad: Joypad) : InputAdapter() {
    val mapping = mapOf(
            Input.Keys.X to Joypad.JoypadKey.A,
            Input.Keys.Z to Joypad.JoypadKey.B,
            Input.Keys.UP to Joypad.JoypadKey.UP,
            Input.Keys.LEFT to Joypad.JoypadKey.LEFT,
            Input.Keys.DOWN to Joypad.JoypadKey.DOWN,
            Input.Keys.RIGHT to Joypad.JoypadKey.RIGHT,
            Input.Keys.ENTER to Joypad.JoypadKey.START,
            Input.Keys.BACKSPACE to Joypad.JoypadKey.SELECT
    )

    override fun keyDown(keycode: Int): Boolean {
        val key = mapping[keycode]
        if (key != null) {
            joypad.keyPressed(key)
            return true
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        val key = mapping[keycode]
        if (key != null) {
            joypad.keyReleased(key)
            return true
        }
        return false
    }
}
