package me.santio.coffee.common.parser

import me.santio.coffee.common.annotations.Optional
import me.santio.coffee.common.models.ParameterPair
import me.santio.coffee.common.models.ResolvedParameter
import org.jetbrains.annotations.Nullable
import java.lang.reflect.Method
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction

/**
 * Handles evaluating parameters.
 */
object ParameterParser {

    /**
     * Resolves the parameters for the method.
     */
    fun resolve(parameter: ParameterPair): ResolvedParameter {
        return ResolvedParameter(
            parameter.kotlin?.name ?: parameter.java.name,
            parameter.java.type,
            isOptional(parameter),
            isInfinite(parameter),
            parameter
        )
    }

    /**
     * Gets the list of parameters for the method.
     * @param method The method to get the parameters for.
     * @return The list of parameters, in a pair holding both the java and kotlin types.
     * @see ParameterPair
     */
    fun getParameters(method: Method): List<ParameterPair> {
        return if (method.kotlinFunction != null) {
            val data = method.kotlinFunction!!
            method.parameters.zip(data.valueParameters)
                .mapIndexed { index, pair -> ParameterPair(index, pair) }
        } else method.parameters.mapIndexed { index, java -> ParameterPair(index, java, null) }
    }

    /**
     * Checks if the parameter is optional.
     */
    private fun isOptional(parameter: ParameterPair): Boolean {
        return parameter.java.isAnnotationPresent(Optional::class.java)
            || parameter.java.isAnnotationPresent(Nullable::class.java)
            || parameter.kotlin?.isOptional
            ?: false
    }

    /**
     * Checks if the parameter is infinite.
     */
    private fun isInfinite(parameter: ParameterPair): Boolean {
        return parameter.kotlin?.isVararg == true || parameter.java.isVarArgs
    }

}