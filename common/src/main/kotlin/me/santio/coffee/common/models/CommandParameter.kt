package me.santio.coffee.common.models

data class CommandParameter(
    val placement: Int,
    val name: String,
    val type: Class<*>,
    val optional: Boolean,
    val infinite: Boolean,
)
