package me.santio.coffee.jda.listeners

import me.santio.coffee.common.Coffee
import me.santio.coffee.common.exception.CommandErrorException
import me.santio.coffee.common.registry.AdapterRegistry
import me.santio.coffee.common.registry.CommandRegistry
import me.santio.coffee.jda.JDAContextData
import me.santio.coffee.jda.gui.button.ButtonContext
import me.santio.coffee.jda.gui.button.ButtonManager
import me.santio.coffee.jda.gui.dropdown.DropdownManager
import me.santio.coffee.jda.gui.modal.ModalManager
import me.santio.coffee.jda.gui.modal.exceptions.ModalAdaptException
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command

class JDAListener(private val bot: JDA): ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {

        val options = event.options.map {
            it.asString
        }

        val command = CommandRegistry.getCommand(event.name) ?: return

        val query = StringBuilder(event.name)
        if (!event.subcommandGroup.isNullOrEmpty()) query.append(" ${event.subcommandGroup}")
        if (!event.subcommandName.isNullOrEmpty()) query.append(" ${event.subcommandName}")

        val bean = command.find(query.toString()) ?: run {
            event.replyEmbeds(
                EmbedBuilder()
                    .setTitle(" ")
                    .setDescription("Failed to find the command!")
                    .setColor(0x67060c)
                    .build()
            ).setEphemeral(true).queue()
            return
        }
        try {
            Coffee.execute(bean, options, JDAContextData(event, bot, command))
        } catch(e: CommandErrorException) {
            event.replyEmbeds(
                EmbedBuilder()
                    .setTitle(" ")
                    .setDescription("""
                        An error occurred while executing the command
                        ```diff
                        - ${e.message}
                        ```
                    """.trimIndent())
                    .setColor(0x67060c)
                    .build()
            ).setEphemeral(true).queue()
        }

    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val command = CommandRegistry.getCommand(event.name) ?: return

        val query = StringBuilder(event.name)
        if (!event.subcommandGroup.isNullOrEmpty()) query.append(" ${event.subcommandGroup}")
        if (!event.subcommandName.isNullOrEmpty()) query.append(" ${event.subcommandName}")

        val bean = command.find(query.toString()) ?: return

        val option = event.focusedOption
        val index = event.options.indexOfFirst {
            it.name.equals(option.name, true) && it.type == option.type
        }

        val argument = bean.parameters.filter {
            it.type != SlashCommandInteractionEvent::class.java
        }[index]

        val adapter = AdapterRegistry.getAdapter(argument.type)

        event.replyChoices(
            adapter.suggest(option.value)
                .filter { it.startsWith(option.value, true) }
                .take(25)
                .map {
                    Command.Choice(it, it)
                }
        ).queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val id = event.componentId
        if (!id.startsWith("coffee-")) return

        val button = ButtonManager.buttons.find { it.id == id }

        if (button == null) {
            event.replyEmbeds(
                EmbedBuilder()
                    .setTitle(" ")
                    .setDescription("The button you clicked has expired!")
                    .setColor(0x67060c)
                    .build()
            ).setEphemeral(true).queue()

            return
        }

        button.consumer(ButtonContext(
            button,
            event
        ))
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        try {
            ModalManager.respond(event.modalId, event)
        } catch(e: ModalAdaptException) {
            event.replyEmbeds(
                EmbedBuilder()
                    .setTitle(" ")
                    .setDescription("""
                    An error occurred while executing the command
                    ```diff
                    - ${e.message}
                    ```
                    """.trimIndent())
                    .setColor(0x67060c)
                    .build()
            ).setEphemeral(true).queue()
        }
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        DropdownManager.get(event.componentId)?.execute(event)
    }

    override fun onEntitySelectInteraction(event: EntitySelectInteractionEvent) {
        DropdownManager.get(event.componentId)?.execute(event)
    }
}