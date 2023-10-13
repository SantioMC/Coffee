package me.santio.coffee.jda.gui.button

import net.dv8tion.jda.api.entities.emoji.EmojiUnion
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.schedule

data class Button(
    var label: String,
    var style: ButtonStyle,
    var disabled: Boolean = false,
    var emote: EmojiUnion? = null,
    var consumer: (ButtonContext) -> Unit
) {
    val id: String = "coffee-${UUID.randomUUID().toString().substring(0, 16)}"

    companion object {
        @JvmStatic
        @JvmName("create")
        @JvmOverloads
        fun create(
            label: String,
            style: ButtonStyle = ButtonStyle.PRIMARY,
            emote: EmojiUnion? = null,
            disabled: Boolean = false,
            expiry: Duration = Duration.of(1, ChronoUnit.MINUTES),
            consumer: (ButtonContext) -> Unit = ::defaultConsumer
        ): Button {
            val button = Button(label, style, disabled, emote, consumer)
            ButtonManager.buttons.add(button)

            // Invalidate the button after 1 minute to prevent memory leaks
            Timer().schedule(expiry.toMillis()) {
                ButtonManager.unregister(button.id)
            }

            return button
        }

        @JvmStatic
        @JvmName("from")
        fun from(
            button: net.dv8tion.jda.api.interactions.components.buttons.Button
        ) = Button(button.label, button.style, button.isDisabled, button.emoji, ::defaultConsumer)

        private fun defaultConsumer(context: ButtonContext) {
            context.event.deferEdit().queue()
        }
    }

    fun build(): net.dv8tion.jda.api.interactions.components.buttons.Button {
        return net.dv8tion.jda.api.interactions.components.buttons.Button.of(
            style,
            id,
            label
        ).withDisabled(disabled).withEmoji(emote)
    }
}