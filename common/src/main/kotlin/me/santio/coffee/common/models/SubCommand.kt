package me.santio.coffee.common.models

import me.santio.coffee.common.exception.CommandErrorException
import me.santio.coffee.common.parser.ClassParser
import me.santio.coffee.common.parser.CommandParser
import java.lang.reflect.Method

data class SubCommand(
    val method: Method,
    val instance: Any,
    val isEntryPoint: Boolean,
    val isAsync: Boolean,
    val parameters: List<CommandParameter>
) {

    fun execute(arguments: List<String>) {
        CommandParser.runAsync {
            @Suppress("NAME_SHADOWING")
            val arguments = arguments.toMutableList()

            val defaults = CommandParser.getAutomaticParameters()
            val types = parameters.take(defaults.size).map { it.type }
            val parameters = parameters.drop(defaults.size)

            val pass = mutableListOf<Any?>()

            // Handle defaults
            for (default in defaults) {
                val type = types.firstOrNull { default.types.contains(it) }
                    ?: throw CommandErrorException("Invalid default parameter type: ${default.javaClass.name}")

                val input = arguments.removeFirstOrNull() ?: throw CommandErrorException("Failed to find value to supply automatic parameter, the implementation is likely broken")
                pass.add(default.handle(type, input))
            }

            // Ensure all arguments are provided
            val requiredArguments = parameters.filter { !it.optional }
            if (arguments.size < requiredArguments.size)
                throw CommandErrorException("Invalid syntax")

            var index = 0;
            for (argument in arguments) {
                val parameter = parameters.getOrNull(index)
                    ?: throw CommandErrorException("Invalid argument $index: $argument")

                val adapter = CommandParser.getAdapter(parameter.type)

                if (!adapter.isValid(argument))
                    throw CommandErrorException(adapter.error.replace("%arg%", argument))

                if (parameter.infinite) {
                    val array = pass.getOrNull(index) as? Array<*> ?: emptyArray<Any?>()
                    val list = array.toMutableList()

                    list.add(adapter.adapt(argument))

                    if (pass.size <= index) pass.add(CommandParser.convertListToArray(list, parameter.type))
                    else pass[index] = CommandParser.convertListToArray(list, parameter.type)
                } else {
                    pass.add(adapter.adapt(argument))
                    index++
                }
            }

            if (isAsync) method.invoke(instance, *pass.toTypedArray())
            else CommandParser.runSync {
                method.invoke(instance, *pass.toTypedArray())
            }
        }
    }

    fun getBaseClass(): Class<*> {
        return ClassParser.getBaseClass(method.declaringClass)
    }

}