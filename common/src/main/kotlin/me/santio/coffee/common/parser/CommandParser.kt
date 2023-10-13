package me.santio.coffee.common.parser

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.impl.DoubleAdapter
import me.santio.coffee.common.adapter.impl.FloatAdapter
import me.santio.coffee.common.adapter.impl.IntegerAdapter
import me.santio.coffee.common.adapter.impl.StringAdapter
import me.santio.coffee.common.annotations.Command
import me.santio.coffee.common.annotations.ParserIgnore
import me.santio.coffee.common.annotations.Sync
import me.santio.coffee.common.async.AsyncDriver
import me.santio.coffee.common.async.DefaultAsyncDriver
import me.santio.coffee.common.exception.CommandValidationException
import me.santio.coffee.common.exception.NoAdapterException
import me.santio.coffee.common.models.CommandParameter
import me.santio.coffee.common.models.Path
import me.santio.coffee.common.models.SubCommand
import org.jetbrains.annotations.Nullable
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Parameter
import java.util.function.Consumer
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction

@Suppress("unused")
object CommandParser {

    private val commands = mutableMapOf<Path, SubCommand>()
    private val adapters = mutableListOf<ArgumentAdapter<*>>(
        IntegerAdapter, StringAdapter, DoubleAdapter, FloatAdapter
    )
    private val registerListeners = mutableListOf<Consumer<List<SubCommand>>>()
    private var asyncDriver: AsyncDriver = DefaultAsyncDriver

    /**
     * Registers a listener that will be called whenever a new command is registered.
     * Once called, a list of all registered commands will be passed to the listener immediately.
     * @param listener The listener to register.
     */
    @JvmStatic
    fun onRegister(listener: Consumer<List<SubCommand>>) {
        listener.accept(commands.values.toList())
        registerListeners.add(listener)
    }

    /**
     * Registers a new asynchronous driver. This will be used to run commands asynchronously.
     * If a driver is already registered, it will be replaced with the new driver.
     * @param driver The driver to register.
     * @see AsyncDriver
     * @see Sync
     */
    @JvmStatic
    fun registerAsyncDriver(driver: AsyncDriver) {
        asyncDriver = driver
    }

    /**
     * Runs the provided runnable through the asynchronous driver, keep in mind that the default
     * driver implementation runs all commands synchronously.
     * @see AsyncDriver
     * @see Sync
     */
    @JvmStatic
    fun runAsync(runnable: Runnable) {
        asyncDriver.runAsync(runnable)
    }

    /**
     * Runs the provided runnable through the asynchronous driver, however this will switch back
     * to the main thread before running the runnable. Keep in mind that the default driver
     * implementation runs all commands synchronously.
     * @see AsyncDriver
     * @see Sync
     */
    @JvmStatic
    fun runSync(runnable: Runnable) {
        asyncDriver.runSync(runnable)
    }

    /**
     * Registers a new argument adapter. This will be used to parse arguments
     * for the given type. If an adapter for the given type already exists,
     * it will be replaced with the new adapter. This change will apply immediately.
     * @param adapters The adapters to register.
     * @see ArgumentAdapter
     */
    @JvmStatic
    fun registerAdapter(vararg adapters: ArgumentAdapter<*>) {
        for (adapter in adapters) {
            this.adapters.removeIf { it.type == adapter.type }
            this.adapters.add(adapter)
        }
    }

    /**
     * Get the adapter for the given type.
     * @param type The type to get the adapter for.
     * @return The adapter for the given type.
     */
    @JvmStatic
    fun getAdapter(type: Class<*>): ArgumentAdapter<*> {
        return adapters.firstOrNull { it.type == type }
            ?: throw NoAdapterException("No adapter found for type ${type.simpleName}")
    }

    /**
     * Checks if the given class is a command class.
     * @param clazz The class to check.
     * @return True if the class is a command class.
     * @see ClassParser.isValid
     */
    @JvmStatic
    fun isCommand(clazz: Class<*>): Boolean = ClassParser.isValid(clazz)

    /**
     * Saves the given path and subcommand to the command list.
     * @param path The path to save.
     * @param command The associated subcommand to save.
     */
    @JvmStatic
    fun registerCommand(path: Path, command: SubCommand) {
        commands[path] = command
        registerListeners.forEach { it.accept(commands.values.toList()) }
    }

    /**
     * Finds the exact command for the given path.
     * @param path The exact path to find.
     * @return The command if found, null otherwise.
     */
    @JvmStatic
    fun getCommand(path: Path): SubCommand? {
        return commands[commands.keys.firstOrNull { it == path }]
    }

    /**
     * Finds the most suitable command for the given path.
     * @param path The path to find.
     * @return The command if found, null otherwise.
     */
    @JvmStatic
    fun findCommand(path: Path): Pair<Path, SubCommand>? {
        var currentPath = path

        while (currentPath.sections.isNotEmpty()) {
            val command = getCommand(currentPath)
            if (command != null) return currentPath to command

            currentPath = currentPath.copy(sections = currentPath.sections.dropLast(1).toMutableList())
        }

        return null
    }

    /**
     * Checks if the given method should be completely ignored by the parser.
     * @param method The method to check.
     * @return True if the method should be ignored.
     * @see ParserIgnore
     */
    @JvmStatic
    private fun ignore(method: Method): Boolean {
        if (method.kotlinFunction != null) {
            val data = method.kotlinFunction!!
            if (data.visibility != KVisibility.PUBLIC || data.isSuspend) return true
        } else {
            if (!Modifier.isPublic(method.modifiers) || Modifier.isStatic(method.modifiers)) return true
        }

//        if (method.parameterCount < automaticParameters.size) return true

//        for (parameter in automaticParameters) {
//            if (!parameter.isValid(method.parameterTypes[automaticParameters.indexOf(parameter)]))
//                return true
//        }

        return method.isAnnotationPresent(ParserIgnore::class.java)
            || method.isAnnotationPresent(JvmStatic::class.java)
    }

    /**
     * Checks if the given class should be completely ignored by the parser.
     * @param clazz The class to check.
     * @return True if the class should be ignored.
     * @see ParserIgnore
     */
    @JvmStatic
    fun ignore(clazz: Class<*>): Boolean {
        if (clazz.kotlin.isCompanion) return true
        if (!Modifier.isPublic(clazz.modifiers) || Modifier.isAbstract(clazz.modifiers)) return true
        return clazz.isAnnotationPresent(ParserIgnore::class.java)
    }

    /**
     * Finds the entry method for the given class. This will either be the method with the name
     * "main", if it doesn't exist it will look for a standalone method, if it still can't find
     * one it will take the first method it finds.
     * @param clazz The class to find the entry method for.
     * @return The entry method.
     */
    @JvmStatic
    fun getEntryMethod(clazz: Class<*>): Method? {
        val methods = clazz.declaredMethods.filter { !ignore(it) }
        return methods
            .filter { it.getAnnotation(Command::class.java) == null }
            .firstOrNull { it.name == "main" || it.name == "execute" }
    }

    /**
     * Parses a method of a class and return parameter information for the method.
     * @param method The method to parse.
     * @return The list of parameters.
     */
    private fun parseMethod(method: Method): SubCommand {
        val arguments = mutableListOf<CommandParameter>()

        val instance = try {
            method.declaringClass.kotlin.objectInstance
                ?: method.declaringClass.getDeclaredConstructor().newInstance()
        } catch(e: IllegalAccessException) {
            throw CommandValidationException("Failed to create instance of class ${method.declaringClass.name}")
        }

        val parameters: List<Pair<Parameter, KParameter?>> = if (method.kotlinFunction != null) {
            val data = method.kotlinFunction!!
            method.parameters.zip(data.valueParameters)
        } else method.parameters.map { it to null }

        for ((placement, parameter) in parameters.withIndex()) {
            val name = parameter.second?.name ?: parameter.first.name
            val optional = parameter.second?.type?.isMarkedNullable ?: parameter.first.type.isAnnotationPresent(Nullable::class.java)
            val infinite = parameter.second?.isVararg ?: parameter.first.isVarArgs

            val individual = parameter.first.type.componentType ?: parameter.first.type
            arguments.add(CommandParameter(placement, name, individual, optional, infinite))
        }

        return SubCommand(
            method,
            instance,
            getEntryMethod(method.declaringClass) == method,
            method.getAnnotation(Sync::class.java) == null,
            arguments
        )
    }

    /**
     * Parses the given class and returns a list of subcommands.
     * @param clazz The class to register.
     * @return The list of subcommands that were parsed.
     */
    @JvmStatic
    fun parseClass(clazz: Class<*>): List<SubCommand> {
        if (!isCommand(clazz)) throw CommandValidationException("Class is not a valid command class. Annotate it with @Command")

        // Get all methods in this class
        val methods = clazz.declaredMethods
            .filter { !ignore(it) }
            .map { parseMethod(it) }.toMutableList()

        // Get all subcommands in inner classes
        methods += clazz.declaredClasses
            .filter { !ignore(it) }
            .flatMap { parseClass(it) }

        return methods
    }

    /**
     * Adapts the given list to an array of the given type, this utilizes the adapters to
     * convert a string to the correct type.
     * @param query The first element of the list.
     * @param list The list to convert.
     * @return The converted array.
     */
    @JvmStatic
    fun adapt(query: String, vararg list: String): Array<Any?> {
        val adapted = mutableListOf<Any?>()

        for (element in listOf(query, *list)) {
            val adapter = adapters.firstOrNull { it.isValid(element) }
            if (adapter == null) throw NoAdapterException("No adapter found for type ${element.javaClass}")
            else adapted.add(adapter.adapt(element))
        }

        return adapted.toTypedArray()
    }

    /**
     * Adapts a given string, separating each token by a space.
     * @param query The string to adapt.
     * @return The adapted array.
     */
    @JvmStatic
    fun adapt(query: String): Array<Any?> {
        val tokens = query.split(" ")
        return adapt(tokens.first(), *tokens.drop(1).toTypedArray())
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> createList(clazz: KClass<out T>, values: List<T?>): ArrayList<T> {
        val type = ArrayList::class.createType(listOf(KTypeProjection.invariant(clazz.starProjectedType)))
        val constructor = type.classifier as KClass<out ArrayList<T>>
        return constructor.java.getConstructor(Collection::class.java).newInstance(values)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> convertListToArray(list: List<T>, type: Class<*>): Array<T?> {
        val array = java.lang.reflect.Array.newInstance(type, list.size) as Array<T?>
        for (i in list.indices) array[i] = list[i]
        return array
    }

}
