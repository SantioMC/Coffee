package me.santio.coffee.jda.gui.button

import me.santio.coffee.jda.editActionComponent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class ButtonContext(
    val button: Button,
    val event: ButtonInteractionEvent
) {
    fun enable() {
        event.message.editActionComponent {
            if (it.id == button.id) button.copy(disabled = false).build()
            else it
        }
    }

    fun disable() {
        event.message.editActionComponent {
            if (it.id == button.id) button.copy(disabled = true).build()
            else it
        }
    }

    fun delete() {
        ButtonManager.unregister(button.id)

        event.message.editActionComponent {
            if (it.id == button.id) null else it
        }
    }
}
