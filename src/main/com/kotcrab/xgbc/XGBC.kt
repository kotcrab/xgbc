package com.kotcrab.xgbc

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.xgbc.ui.DebuggerWindow

/** @author Kotcrab */
class XGBC : ApplicationAdapter() {
    private lateinit var stage: Stage
    private lateinit var emulator: Emulator

    override fun create() {
        VisUI.load()
        stage = Stage(ScreenViewport());
        val root = VisTable()
        root.setFillParent(true)

        stage.addActor(root)

        Gdx.input.inputProcessor = stage

        emulator = Emulator(Gdx.files.internal("rom/tetris.gb"));

        stage.addActor(DebuggerWindow(emulator))
    }

    override fun resize(width: Int, height: Int) {
        if (width == 0 && height == 0) return;
        stage.viewport.update(width, height, true);
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.deltaTime, 1 / 30f));
        stage.draw()
    }

    override fun dispose() {
        VisUI.dispose()
        stage.dispose()
    }
}
