package me.santio.coffee.jda

import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.common.models.tree.CommandTree
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * Additional data for all parameter contexts.
 */
data class JDAContextData(
    val event: SlashCommandInteractionEvent,
    val bot: JDA,
    override val tree: CommandTree<*>
): ContextData(tree)