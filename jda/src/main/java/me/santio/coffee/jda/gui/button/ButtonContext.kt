package me.santio.coffee.jda.gui.button

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class ButtonContext(
    val button: Button,
    val event: ButtonInteractionEvent
) {
    fun disable() {
        event.editButton(
            button.copy(disabled = true).build()
        ).queue()
        this.unregister()
    }

    fun enable() {
        event.editButton(
            button.copy(disabled = false).build()
        ).queue()
        this.unregister()
    }

    fun unregister() {
        ButtonManager.unregister(button.id)
    }
}
