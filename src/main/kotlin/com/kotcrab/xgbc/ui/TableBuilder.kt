/*
 * Copyright 2014-2016 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.xgbc.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.kotcrab.vis.ui.widget.VisTable

/**
 * Table in tables. Simplified.
 * @author Kotcrab
 */
object TableBuilder {
    fun build(text: String, labelWidth: Int, actor: Actor): VisTable {
        val table = VisTable(true)
        table.add(text).width(labelWidth.toFloat())
        table.add(actor)
        return table
    }

    fun build(vararg actors: Actor): VisTable {
        return build(VisTable(true), *actors)
    }

    fun build(rightSpacing: Int, vararg actors: Actor): VisTable {
        val table = VisTable(true)
        table.defaults().spaceRight(rightSpacing.toFloat())
        return build(table, *actors)
    }

    fun build(target: VisTable, vararg actors: Actor): VisTable {
        for (actor in actors) target.add(actor)
        return target
    }

}
