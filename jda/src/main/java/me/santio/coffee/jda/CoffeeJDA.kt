package me.santio.coffee.jda

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.async.AsyncDriver
import me.santio.coffee.common.async.ExecutorAsyncDriver
import me.santio.coffee.common.models.CoffeeBundle
import me.santio.coffee.common.parameter.ParameterContext
import me.santio.coffee.common.registry.CommandRegistry
import me.santio.coffee.jda.adapters.*
import me.santio.coffee.jda.listeners.JDAListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * The implementation for JDA.
 * @param bot The JDA instance.
 * @param slash Should we use slash commands, or the old text commands way
 * NOTE: Only slash commands are supported at the moment
 */
open class CoffeeJDA(private val bot: JDA, slash: Boolean = true): CoffeeBundle() {

    override val adapters: List<ArgumentAdapter<*>> = listOf(
        UserAdapter, MemberAdapter, RoleAdapter,
        TextChannelAdapter, VoiceChannelAdapter, GuildChannelAdapter
    )
    override val asyncDriver: AsyncDriver = ExecutorAsyncDriver

    override fun handleParameter(context: ParameterContext<*>) {
        val data = context.data as? JDAContextData ?: return
        if (context.type == SlashCommandInteractionEvent::class.java) {
            context.respond(data.event)
        }
    }

    override fun ready() {
        CommandRegistry.onRegister { command ->
            JDACommand.register(bot, command)
        }

        bot.addEventListener(JDAListener(bot))
    }

}