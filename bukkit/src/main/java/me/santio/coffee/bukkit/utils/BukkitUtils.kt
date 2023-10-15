package me.santio.coffee.bukkit.utils

import me.santio.coffee.bukkit.BukkitContextData
import me.santio.coffee.bukkit.annotations.Permission
import me.santio.coffee.bukkit.builders.CommandBuilder
import me.santio.coffee.common.Coffee
import me.santio.coffee.common.exception.CommandErrorException
import me.santio.coffee.common.models.tree.CommandTree
import me.santio.coffee.common.registry.CommandRegistry
import me.santio.coffee.common.resolvers.AnnotationResolver
import me.santio.coffee.common.resolvers.Scope
import org.bukkit.command.*

internal object BukkitUtils {

    private val commandsRegistered = mutableSetOf<String>()

    /**
     * Gets the command map from the server.
     * @return The command map.
     * @see CommandMap
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getCommandMap(): CommandMap {
        val server = org.bukkit.Bukkit.getServer()

        val field = server.javaClass.getDeclaredField("commandMap")
        field.isAccessible = true

        return field.get(server) as CommandMap
    }

    /**
     * Unregisters a command from a server.
     * @param command The command to unregister.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun unregister(command: CommandTree<*>) {
        val name = command.name

        if (!commandsRegistered.contains(name)) return
        commandsRegistered.remove(name)

        getCommandMap().getCommand(name)?.unregister(getCommandMap())
    }

    /**
     * Registers a command to a server.
     */
    fun register(command: CommandTree<*>) {
        val name = command.name

        if (commandsRegistered.contains(name)) unregister(command)
        commandsRegistered.add(name)

        val builder = CommandBuilder.from(command)
        getCommandMap().register(builder.namespace, object : Command(
            builder.name,
            builder.description(),
            "/${builder.name}",
            builder.aliases()
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
            try {
                val tree = CommandRegistry.getCommand(command.name) ?: return true
                val query = "${command.name} ${args.joinToString(" ")}"
                val bean = tree.find(query) ?: return true

                val permission = AnnotationResolver.getAnnotation(bean, Permission::class.java, Scope.ALL)?.value?.let {
                    if (it == "none" || it.isEmpty()) null else it
                }

                if (permission != null && !sender.hasPermission(permission)) {
                    sender.sendMessage("§cYou do not have permission to execute this command.")
                    return true
                }

                // Execute command
                Coffee.execute(
                    query,
                    BukkitContextData(sender, tree)
                )
            } catch(e: Exception) {
                if (e is CommandErrorException) {
                    sender.sendMessage("§c" + e.message)
                } else {
                    sender.sendMessage("§cAn error was thrown while executing the command")
                    sender.sendMessage("§c${e.javaClass.simpleName}: ${e.message}")

                    println("An error was thrown while executing the command")
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