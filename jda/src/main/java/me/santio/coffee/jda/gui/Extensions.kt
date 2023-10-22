@file:Suppress("unused")

package me.santio.coffee.jda.gui

import me.santio.coffee.jda.gui.button.Button
import me.santio.coffee.jda.gui.modal.ModalManager
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest
import java.util.function.BiConsumer

fun ReplyCallbackAction.addActionRow(vararg buttons: Button): ReplyCallbackAction {
    return this.addActionRow(
        *buttons.map {
            it.build()
        }.toTypedArray()
    )
}

fun <T: Any> IModalCallback.showModal(modal: Class<T>, callback: BiConsumer<T, ModalInteractionEvent>) {
    ModalManager.showModal(this, modal, callback)
}