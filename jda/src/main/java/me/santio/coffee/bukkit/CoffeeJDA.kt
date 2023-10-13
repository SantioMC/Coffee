package me.santio.coffee.bukkit

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.async.AsyncDriver
import me.santio.coffee.common.async.ExecutorAsyncDriver
import me.santio.coffee.common.models.CoffeeBundle
import me.santio.coffee.common.parameter.ParameterContext
import me.santio.coffee.common.parser.CommandParser
import net.dv8tion.jda.api.JDA

/**
 * The implementation for JDA.
 * @param bot The JDA instance.
 * @param slash Should we use slash commands, or the old text commands way
 * NOTE: Only slash commands are supported at the moment
 */
class CoffeeJDA(bot: JDA, slash: Boolean = true): CoffeeBundle() {

    override val adapters: List<ArgumentAdapter<*>> = listOf()
    override val asyncDriver: AsyncDriver = ExecutorAsyncDriver

    override fun handleParameter(context: ParameterContext<*>) {
        // Nothing
    }

    init {
        CommandParser.onRegister { commands ->
            commands.forEach {
                JDACommand.register(bot, it)
            }
        }
    }

}