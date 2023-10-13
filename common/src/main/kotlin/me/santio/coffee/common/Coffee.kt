package me.santio.coffee.common

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.common.annotations.Command
import me.santio.coffee.common.exception.CommandErrorException
import me.santio.coffee.common.models.CoffeeBundle
import me.santio.coffee.common.models.Path
import me.santio.coffee.common.parser.CommandParser
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
            val subcommands = CommandParser.parseClass(command)
            subcommands.forEach {
                CommandParser.registerCommand(Path.from(it), it)
            }
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
        val annotated: Set<Class<*>> = Reflections(`package`)
            .getTypesAnnotatedWith(Command::class.java)

        return this.brew(*annotated.toTypedArray())
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

            override fun adapt(arg: String, context: ContextData): T? {
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
        CommandParser.registerAdapter(*bundle.adapters.toTypedArray())
        CommandParser.registerAsyncDriver(bundle.asyncDriver)
        Coffee.bundle = bundle

        return this
    }

    /**
     * Evaluates a command and executes it if possible.
     * @param path The path to evaluate.
     * @param data The context data to pass to the command.
     * @return True if the command exists, false otherwise. This does not mean that the command was
     * executed successfully or not.
     * @throws CommandErrorException If the command throws an error.
     */
    @JvmStatic
    fun execute(path: Path, data: ContextData): Boolean {
        val command = CommandParser.findCommand(path) ?: return false
        val commandPath = command.first

        val arguments = path.toString()
            .substring(commandPath.toString().length)
            .trim()
            .split(" ")
            .filter { it.isNotEmpty() }

        execute(commandPath, arguments, data)
        return true
    }

    /**
     * Evaluates a command and executes it if possible.
     * @param path The path to evaluate.
     * @param arguments The arguments to pass to the command.
     * @param data The context data to pass to the command.
     * @return True if the command exists, false otherwise. This does not mean that the command was
     * executed successfully or not.
     * @throws CommandErrorException If the command throws an error.
     */
    @JvmStatic
    fun execute(path: Path, arguments: List<String>, data: ContextData): Boolean {
        val command = CommandParser.findCommand(path) ?: return false

        try {
            command.second.execute(arguments, data)
        } catch (e: Exception) {
            if (e is CommandErrorException) throw e
            else println("Failed to execute command: ${e.message}")
        }

        return true
    }

}