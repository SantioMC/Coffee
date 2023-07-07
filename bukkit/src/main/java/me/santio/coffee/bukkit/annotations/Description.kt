package me.santio.coffee.bukkit.annotations

/**
 * Represents a description for a command.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Description(
    val value: String
)
