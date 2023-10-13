package me.santio.coffee.bukkit.annotations

/**
 * The description of the command.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Description(
    val value: String
)
