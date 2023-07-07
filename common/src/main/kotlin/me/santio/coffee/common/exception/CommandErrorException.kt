package me.santio.coffee.common.exception

class CommandErrorException(override val message: String? = "Failed to execute command"): Exception(message) {
    constructor(cause: Throwable): this("Failed to execute command: ${cause.message}")
}