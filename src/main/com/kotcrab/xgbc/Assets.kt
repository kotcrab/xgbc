package com.kotcrab.xgbc

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

/** @author Kotcrab */
object Assets {
    private lateinit var ui: TextureAtlas

    fun load() {
        ui = TextureAtlas(Gdx.files.internal("gfx/ui.atlas"))
    }

    fun dispose() {
        ui.dispose()
    }

    fun get(icon: Icon): Drawable {
        return TextureRegionDrawable(ui.findRegion(icon.iconName))
    }
}

enum class Icon(val iconName: String) {
    BREAKPOINT("breakpoint"), CURRENT_LINE("current-line")
}
