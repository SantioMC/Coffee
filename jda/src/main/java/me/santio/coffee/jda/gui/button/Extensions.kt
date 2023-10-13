@file:Suppress("unused")

package me.santio.coffee.jda.gui.button

import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction

fun ReplyCallbackAction.addActionRow(vararg buttons: Button): ReplyCallbackAction {
    return this.addActionRow(
        *buttons.map {
            it.build()
        }.toTypedArray()
    )
}