package me.santio.coffee.common.models

/**
 * Represents a parsed parameter.
 * @param name The name of the parameter.
 * @param type The type of the parameter.
 * @param optional Whether the parameter is optional.
 * @param infinite Whether the parameter is infinite.
 * @param parameter The [ParameterPair] that this parameter was parsed from, this also contains the placement of the parameter.
 */
data class ResolvedParameter(
    val name: String,
    val type: Class<*>,
    val optional: Boolean,
    val infinite: Boolean,
    val parameter: ParameterPair
)
