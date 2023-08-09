package me.santio.coffee.common

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.models.CoffeeBundle
import me.santio.coffee.common.models.Path
import me.santio.coffee.common.parser.CommandParser
import java.util.function.Function

object Coffee {
    /**
     * Registers a class as a command and makes it available to be executed.
     * @param commands The classes to register.
     */
    @JvmStatic
    fun brew(vararg commands: Class<*>): Coffee {
        for (command in commands) {
            val subcommands = CommandParser.parseClass(command)
            subcommands.forEach {
                CommandParser.registerCommand(Path.from(it), it)
            }
        }

        return this
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
        CommandParser.registerAdapter(*adapter)
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

            override fun adapt(arg: String): T? {
                return adapter.apply(arg)
            }
        }

        CommandParser.registerAdapter(argumentAdapter)
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
        CommandParser.registerAutomaticParameter(*bundle.automaticParameters.toTypedArray())
        CommandParser.registerAdapter(*bundle.adapters.toTypedArray())
        CommandParser.registerAsyncDriver(bundle.asyncDriver)
        return this
    }

    /**
     * Evaluates a command and executes it if possible.
     * @param path The path to evaluate.
     * @param defaults The default values to pass for every command, these will be handled
     * by the automatic parameters.
     * @return True if the command exists, false otherwise. This does not mean that the command was
     * executed successfully or not.
     */
    @JvmStatic
    fun execute(path: Path): Boolean {
        val command = CommandParser.findCommand(path) ?: return false
        val commandPath = command.first

        val arguments = path.toString()
            .substring(commandPath.toString().length)
            .trim()
            .split(" ")
            .filter { it.isNotEmpty() }

        command.second.execute(arguments)
        return true
    }
}