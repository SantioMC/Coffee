package me.santio.coffee.common.exception

class NoAdapterException(override val message: String? = "Failed to find adapter"): Exception(message)