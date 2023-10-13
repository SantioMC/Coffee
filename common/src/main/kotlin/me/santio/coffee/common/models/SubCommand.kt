package me.santio.coffee.common.models

import me.santio.coffee.common.Coffee
import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.common.exception.CommandErrorException
import me.santio.coffee.common.parameter.ParameterContext
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

    private fun createContext(parameter: CommandParameter, data: ContextData): ParameterContext<*> {
        return ParameterContext(
            parameter.placement,
            method,
            parameter.type,
            parameter.name,
            data
        )
    }

    fun execute(arguments: List<String>, data: ContextData) {

        val bundle = Coffee.bundle
        val response: MutableList<Any?> = mutableListOf()
        var argumentPointer = 0

        for (parameter in parameters) {
            // Get the current associated argument
            fun argument(): String {
                return arguments.getOrNull(argumentPointer)
                    ?: throw CommandErrorException("Missing argument, no value provided for ${parameter.name}")
            }

            // Build a context for the parameter
            val context = createContext(parameter, data)
            bundle.handleParameter(context)

            // Handle the parameter accordingly
            if (context.responded) { // Handled by implementation
                if (context.consume) argumentPointer++
                response.add(context.response)
            } else { // Handle by adapter

                // Get the adapter and check if the adapter can properly handle the argument
                val adapter = CommandParser.getAdapter(parameter.type)
                if (!adapter.isValid(argument()))
                    throw CommandErrorException(adapter.error.replace("%arg%", argument()))

                if (parameter.infinite) {
                    val remaining = arguments.subList(argumentPointer, arguments.size)
                        .filter { adapter.isValid(it) }
                        .map { adapter.adapt(it) }

                    response.add(CommandParser.convertListToArray(remaining, parameter.type))
                } else {
                    response.add(adapter.adapt(argument()))
                    argumentPointer++
                }

            }

        }

        val run = Runnable { method.invoke(instance, *response.toTypedArray()) }

        if (isAsync) CommandParser.runAsync(run)
        else CommandParser.runSync(run)
    }

    fun getBaseClass(): Class<*> {
        return ClassParser.getBaseClass(method.declaringClass)
    }

}