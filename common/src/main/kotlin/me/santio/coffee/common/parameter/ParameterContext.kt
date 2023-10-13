package me.santio.coffee.common.parameter

import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.common.exception.CommandErrorException
import java.lang.reflect.Method

@Suppress("MemberVisibilityCanBePrivate", "unused")
/**
 * The context of a parameter, this is used to add custom logic to parameters.
 * @param placement The placement of the parameter in the method command.
 * @param type The type of the argument.
 * @param input The input passed into the parameter.
 */
data class ParameterContext<T>(
    val placement: Int,
    val method: Method,
    val type: Class<T>,
    val input: String,
    val data: ContextData? = null
) {
    /**
     * The response provided by a coffee implementation
     */
    var response: T? = null
        private set

    /**
     * Whether the input should be consumed.
     */
    internal var consume: Boolean = false
        private set

    /**
     * Whether the parameter has been responded to, or if the default behaviour should be used.
     */
    val responded: Boolean
        get() = response != null

    /**
     * Whether the parameter is the first parameter in the method command.
     */
    val isFirst: Boolean
        get() = placement == 0

    /**
     * Responds to the parameter with a value, this will override the default behaviour.
     * @param value The value to respond with.
     * @param consume Whether to pass the input to the next parameter.
     * If this is false, the input will be passed to the next parameter, if true,
     * the input will be consumed (essentially the arguments being shifted)
     * @throws CommandErrorException if an error message should be sent to the sender.
     */
    @Suppress("UNCHECKED_CAST")
    fun respond(value: Any, consume: Boolean = false) {
        this.consume = consume
        response = value as T
    }
}
