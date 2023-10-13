package me.santio.coffee.common.models

import java.lang.reflect.Parameter

data class CommandParameter(
    val placement: Int,
    val name: String,
    val type: Class<*>,
    val optional: Boolean,
    val infinite: Boolean,
    val parameter: Parameter
)
