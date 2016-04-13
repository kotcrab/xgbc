package com.kotcrab.xgbc

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.xgbc.ui.DebuggerWindow
import com.kotcrab.xgbc.ui.EmulatorWindow

/** @author Kotcrab */
class XGBC : ApplicationAdapter() {
    private lateinit var stage: Stage
    private lateinit var emulator: Emulator

    override fun create() {
        VisUI.load()
        Assets.load()
        stage = Stage(ScreenViewport())
        val root = VisTable()
        root.setFillParent(true)

        stage.addActor(root)

        Gdx.input.inputProcessor = stage

        //emulator = Emulator(Gdx.files.internal("rom/test/01-special.gb")) //pass
        emulator = Emulator(Gdx.files.internal("rom/test/02-interrupts.gb")) //pass
        //emulator = Emulator(Gdx.files.internal("rom/test/03-op sp,hl.gb")) //pass
        //emulator = Emulator(Gdx.files.internal("rom/test/04-op r,imm.gb")) //pass
        //emulator = Emulator(Gdx.files.internal("rom/test/05-op rp.gb")) //pass
        //emulator = Emulator(Gdx.files.internal("rom/test/06-ld r,r.gb")) //pass
        //emulator = Emulator(Gdx.files.internal("rom/test/07-jr,jp,call,ret,rst.gb")) //doesn't finish
        //emulator = Emulator(Gdx.files.internal("rom/test/08-misc instrs.gb")) //pass
        //emulator = Emulator(Gdx.files.internal("rom/test/09-op r,r.gb")) //fault
        //emulator = Emulator(Gdx.files.internal("rom/test/10-bit ops.gb")) //pass
        //emulator = Emulator(Gdx.files.internal("rom/test/11-op a,(hl).gb")) //fault

        //emulator = Emulator(Gdx.files.internal("rom/test/mem_timing.gb"))
        //emulator = Emulator(Gdx.files.internal("rom/test/cpu_instrs.gb"))
        //emulator = Emulator(Gdx.files.internal("rom/tetris.gb"))

        stage.addActor(EmulatorWindow(emulator))
        val debug = true;
        var debuggerWindow: DebuggerWindow? = null
        if (debug) {
            debuggerWindow = DebuggerWindow(emulator);
            stage.addActor(debuggerWindow)
        }

        stage.addListener(object : InputListener() {
            override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
                if (keycode == Input.Keys.F1 && debuggerWindow != null) {
                    (debuggerWindow as DebuggerWindow).remove()
                    emulator.reset()
                    debuggerWindow = DebuggerWindow(emulator);
                    stage.addActor(debuggerWindow)

                }
                return super.keyDown(event, keycode)
            }
        })

    }

    override fun resize(width: Int, height: Int) {
        if (width == 0 && height == 0) return
        stage.viewport.update(width, height, true)
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(Math.min(Gdx.graphics.deltaTime, 1 / 30f))
        stage.draw()

        emulator.update()
    }

    override fun dispose() {
        stage.dispose()
        Assets.dispose()
        VisUI.dispose()
    }
}

fun main(args: Array<String>) {
    val config = Lwjgl3ApplicationConfiguration()
    config.setWindowedMode(1280, 720)
    Lwjgl3Application(XGBC(), config)
}
