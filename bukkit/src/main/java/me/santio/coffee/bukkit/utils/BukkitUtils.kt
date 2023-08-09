package me.santio.coffee.bukkit.utils

import me.santio.coffee.bukkit.SenderAutomaticParameter
import me.santio.coffee.bukkit.builders.CommandBuilder
import me.santio.coffee.common.Coffee
import me.santio.coffee.common.exception.CommandErrorException
import me.santio.coffee.common.models.Path
import me.santio.coffee.common.models.SubCommand
import org.bukkit.command.*

internal object BukkitUtils {

    private val commandsRegistered = mutableSetOf<String>()

    /**
     * Gets the command map from the server.
     * @return The command map.
     * @see CommandMap
     */
    fun getCommandMap(): CommandMap {
        val server = org.bukkit.Bukkit.getServer()

        val field = server.javaClass.getDeclaredField("commandMap")
        field.isAccessible = true

        return field.get(server) as CommandMap
    }

    /**
     * Registers a command to a server.
     */
    fun register(subcommand: SubCommand) {
        val path = Path.from(subcommand)
        val name = path.sections.first().name

        if (commandsRegistered.contains(name)) return
        commandsRegistered.add(name)

        val command = CommandBuilder.from(subcommand)
        getCommandMap().register(command.namespace, object : Command(
            command.name,
            command.description(),
            "/${command.name}",
            command.aliases()
        ) {
            override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
                CoffeeCommandHandler.onCommand(sender, this, commandLabel, args)
                return true
            }

            override fun tabComplete(
                sender: CommandSender,
                alias: String,
                args: Array<out String>
            ): MutableList<String> {
                return CoffeeCommandHandler.onTabComplete(sender, this, alias, args)
            }
        })
    }

    object CoffeeCommandHandler: CommandExecutor, TabCompleter {
        override fun onCommand(
            sender: CommandSender,
            command: Command,
            label: String,
            args: Array<out String>
        ): Boolean {
            val builder = StringBuilder(label).append(" ")

            if (sender is ConsoleCommandSender) builder.append(SenderAutomaticParameter.INTERNAL_CONSOLE)
            else builder.append(sender.name)

            builder.append(" ")
            builder.append(args.joinToString(" "))

            val path = Path.from(builder.toString())

            try {
                Coffee.execute(path)
            } catch(e: Exception) {
                if (e is CommandErrorException) {
                    sender.sendMessage("§c" + e.message)
                } else {
                    sender.sendMessage("§cAn error was thrown while executing the command")
                    sender.sendMessage("§c${e.javaClass.simpleName}: ${e.message}")
                    e.printStackTrace()
                }
            }

            return true
        }

        override fun onTabComplete(
            sender: CommandSender,
            command: Command,
            alias: String,
            args: Array<out String>
        ): MutableList<String> {
            return mutableListOf()
        }

    }

}