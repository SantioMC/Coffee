package me.santio.coffee.bukkit

import me.santio.coffee.bukkit.adapters.PlayerAdapter
import me.santio.coffee.bukkit.annotations.Sender
import me.santio.coffee.bukkit.utils.BukkitUtils
import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.async.AsyncDriver
import me.santio.coffee.common.exception.CommandErrorException
import me.santio.coffee.common.models.CoffeeBundle
import me.santio.coffee.common.parameter.ParameterContext
import me.santio.coffee.common.parser.CommandParser
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class CoffeeBukkit(plugin: JavaPlugin): CoffeeBundle() {
    override val adapters: List<ArgumentAdapter<*>> = listOf(PlayerAdapter)
    override val asyncDriver: AsyncDriver = BukkitAsyncDriver(plugin)

    private val senderTypes = listOf(ConsoleCommandSender::class.java, CommandSender::class.java, Player::class.java)

    override fun handleParameter(context: ParameterContext<*>) {
        val injectSender = context.isFirst || context.method.getAnnotation(Sender::class.java) != null
        if (injectSender && context.type in senderTypes) {
            handleSender(context)
        }
    }

    init {
        CommandParser.onRegister { it.forEach(BukkitUtils::register) }
    }

    private fun handleSender(context: ParameterContext<*>) {
        val data = context.data as? BukkitContextData ?: throw IllegalStateException("BukkitContextData is not present.")

        when (context.type) {
            CommandSender::class.java -> context.respond(data.sender)
            ConsoleCommandSender::class.java -> {
                if (data.sender !is ConsoleCommandSender) throw CommandErrorException("This command is limited to the console.")
                context.respond(data.sender)
            }
            Player::class.java -> {
                if (data.sender !is Player) throw CommandErrorException("You must be a player to execute this command.")
                context.respond(data.sender)
            }
        }
    }

}