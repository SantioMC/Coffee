package me.santio.coffee.common.exception

class CommandValidationException(override val message: String? = "Failed to validate command"): Exception(message)