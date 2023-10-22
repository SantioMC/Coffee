package me.santio.coffee.common

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.common.annotations.Command
import me.santio.coffee.common.exception.CommandErrorException
import me.santio.coffee.common.models.CoffeeBundle
import me.santio.coffee.common.models.tree.Bean
import me.santio.coffee.common.parser.CommandParser
import me.santio.coffee.common.registry.AdapterRegistry
import me.santio.coffee.common.registry.CommandRegistry
import org.reflections.Reflections
import java.util.function.Function

@Suppress("unused")
object Coffee {

    internal lateinit var bundle: CoffeeBundle

    /**
     * Registers a class as a command and makes it available to be executed.
     * @param commands The classes to register.
     * @return The Coffee object instance.
     */
    @JvmStatic
    fun brew(vararg commands: Class<*>): Coffee {
        for (command in commands) {
            CommandRegistry.register(CommandParser.parse(command))
        }

        return this
    }

    /**
     * Registers all classes in a package as commands and makes it available to be executed.
     * Any class that is not annotated with [Command] will be ignored.
     * @param pkg The package to scan for classes to register.
     * @return The Coffee object instance.
     */
    @JvmStatic
    fun brew(`package`: String): Coffee {
        val annotated: List<Class<*>> = Reflections(`package`)
            .getTypesAnnotatedWith(Command::class.java)
            .filter { CommandParser.isValid(it) }

        return this.brew(*annotated.toTypedArray())
    }

    /**
     * Spill the coffee, this will unregister all existing commands.
     */
    @JvmStatic
    fun spill() {
        CommandRegistry.dump()
    }

    /**
     * Registers an argument adapter to be used when parsing arguments. If you want a simplier way
     * to register adapters, use [bind]. This will replace any adapter that is already registered to
     * the same class.
     * @param adapter The adapter to register.
     * @see bind
     */
    @JvmStatic
    fun adapter(vararg adapter: ArgumentAdapter<*>): Coffee {
        AdapterRegistry.registerAdapter(*adapter)
        return this
    }

    /**
     * Binds a class to an adapter, if the class is already bound, it will be replaced.
     * @param clazz The class to bind.
     * @param adapter The adapter to bind the class to.
     */
    @JvmStatic
    fun <T: Any> bind(clazz: Class<T>, adapter: Function<String, T?>): Coffee {
        val argumentAdapter = object : ArgumentAdapter<T>() {
            override val type: Class<T> = clazz

            override fun adapt(arg: String, context: ContextData): T? {
                return adapter.apply(arg)
            }
        }

        AdapterRegistry.registerAdapter(argumentAdapter)
        return this
    }

    /**
     * Imports a bundle into Coffee, this is used to load in implementations.
     * @param bundle The bundle to import.
     * @see CoffeeBundle
     */
    @JvmStatic
    @JvmName("bundle")
    fun <B: CoffeeBundle> import(bundle: B): Coffee {
        AdapterRegistry.registerAdapter(*bundle.adapters.toTypedArray())
        Coffee.bundle = bundle
        bundle.ready()

        return this
    }

    /**
     * A nice easy way to execute commands if you don't want to code your own system to do so.
     * @param query The full command with arguments (ex: /math sum 1 2).
     * @param data The context data to pass to the command.
     * @return True if the command exists, false otherwise. This does not mean that the command was
     * executed successfully or not.
     * @throws CommandErrorException If the command throws an error.
     */
    @JvmStatic
    fun execute(query: String, data: ContextData): Boolean {
        val tree = CommandRegistry.getCommand(query.split(" ")[0]) ?: return false
        val command = tree.find(query) ?: return false

        val arguments = query
            .substring(command.fullPath.length)
            .trim()
            .split(" ")
            .filter { it.isNotEmpty() }

        this.execute(command, arguments, data)
        return true
    }

    /**
     * Evaluates a command bean and does some error handling.
     * @param command The command bean to be executed.
     * @param arguments The arguments (in string format) to be passed to the command.
     * @param data The context data to pass to the command.
     * @throws CommandErrorException If the command throws an error.
     */
    @JvmStatic
    fun execute(command: Bean, arguments: List<String>, data: ContextData) {
        try {
            command.execute(arguments, data)
        } catch (e: Exception) {
            if (e is CommandErrorException) throw e
            else {
                println("Failed to execute command: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}