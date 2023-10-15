package me.santio.coffee.common.models

import me.santio.coffee.common.parser.ParameterParser
import java.lang.reflect.Parameter
import kotlin.reflect.KParameter

/**
 * A pair of parameters, one java and one kotlin.
 * @param placement The index of the parameter.
 * @param java The java parameter.
 * @param kotlin The kotlin parameter.
 */
@Suppress("unused")
data class ParameterPair(
    val placement: Int,
    val java: Parameter,
    val kotlin: KParameter?
) {

    /**
     * Whether this is the first parameter.
     */
    val isFirst: Boolean
        get() = placement == 0

    /**
     * Resolves the parameter pair into a [ResolvedParameter].
     * @return The resolved parameter.
     * @see ParameterParser.resolve
     */
    fun resolve(): ResolvedParameter {
        return ParameterParser.resolve(this)
    }

    companion object {
        @JvmStatic
        @JvmName("fromPair")
        operator fun invoke(index: Int, pair: Pair<Parameter, KParameter?>): ParameterPair {
            return ParameterPair(index, pair.first, pair.second)
        }
    }
}