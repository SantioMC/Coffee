package me.santio.coffee.common.registry

import me.santio.coffee.common.Coffee
import me.santio.coffee.common.models.tree.CommandTree
import java.util.function.Consumer

/**
 * Holds and manages all commands registered in Coffee
 */
object CommandRegistry {

    private val commands = mutableListOf<CommandTree<*>>()
    private val registerEvents = mutableListOf<Consumer<(CommandTree<*>)>>()
    private val unregisterEvents = mutableListOf<Consumer<(CommandTree<*>)>>()

    /**
     * Registers a new command, if a command by the same name is already registered, it will be overriden.
     * @param command The command to register.
     */
    @JvmStatic
    fun register(command: CommandTree<*>) {
        if (isRegistered(command.name)) unregister(getCommand(command.name)!!)
        commands.add(command)

        registerEvents.forEach { it.accept(command) }
        println("Registered command: ${command.name}, tree size: ${command.all().size}")
    }

    /**
     * Unregisters a command.
     * @param command The command to unregister.
     */
    @JvmStatic
    fun unregister(command: CommandTree<*>) {
        if (!isRegistered(command.name)) return
        commands.remove(command)

        unregisterEvents.forEach { it.accept(command) }
    }

    /**
     * Clears all registered commands.
     * @see Coffee.spill
     */
    fun dump() {
        commands.forEach(CommandRegistry::unregister)
    }

    /**
     * Checks if a command is registered.
     * @param name The name of the command to check.
     */
    @JvmStatic
    fun isRegistered(name: String): Boolean {
        return commands.any { it.name == name }
    }

    /**
     * Gets a command by its name.
     * @param name The name of the command to get.
     * @return The command tree.
     */
    @JvmStatic
    fun getCommand(name: String): CommandTree<*>? {
        return commands.firstOrNull { it.name == name }
    }

    /**
     * Gets all registered commands.
     * @return A list of all registered command trees.
     */
    @JvmStatic
    fun all(): List<CommandTree<*>> = commands.toList()

    /**
     * Listen to when a command is registered.
     * @param consumer The consumer to call when a command is registered.
     */
    @JvmStatic
    fun onRegister(consumer: Consumer<(CommandTree<*>)>) {
        registerEvents.add(consumer)
    }

    /**
     * Listen to when a command is unregistered.
     * @param consumer The consumer to call when a command is unregistered.
     */
    @JvmStatic
    fun onRemove(consumer: Consumer<(CommandTree<*>)>) {
        unregisterEvents.add(consumer)
    }

}