package me.santio.coffee.common.models.tree

import me.santio.coffee.common.Coffee
import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.common.exception.CommandErrorException
import me.santio.coffee.common.models.ResolvedParameter
import me.santio.coffee.common.parameter.ParameterContext
import me.santio.coffee.common.registry.AdapterRegistry
import me.santio.coffee.common.utils.ReflectionUtils
import java.lang.reflect.Method

/**
 * Represents a command (named coffee bean).
 */
data class Bean(
    override val aliases: List<String>,
    val isAsync: Boolean,
    val parameters: List<ResolvedParameter>,
    val instance: Any,
    val method: Method,
    override val path: String
): Leaf(method, path) {
    init {
        if (aliases.isEmpty()) throw IllegalArgumentException("Aliases cannot be empty")
    }

    private fun createContext(parameter: ResolvedParameter, data: ContextData): ParameterContext<*> {
        return ParameterContext(
            parameter.parameter.placement,
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
                val adapter = AdapterRegistry.getAdapter(parameter.type, argument())
                if (!adapter.isValid(argument(), data))
                    throw CommandErrorException(adapter.error.replace("%arg%", argument()))

                if (parameter.infinite) {
                    val remaining = arguments.subList(argumentPointer, arguments.size)
                        .filter { adapter.isValid(it, data) }
                        .map { adapter.adapt(it, data) }

                    response.add(ReflectionUtils.convertListToArray(remaining, parameter.type))
                } else {
                    response.add(adapter.adapt(argument(), data))
                    argumentPointer++
                }

            }

        }

        val run = Runnable { method.invoke(instance, *response.toTypedArray()) }

        if (isAsync) Coffee.bundle.asyncDriver.runAsync(run)
        else Coffee.bundle.asyncDriver.runSync(run)
    }

}