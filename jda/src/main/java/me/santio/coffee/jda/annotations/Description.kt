package me.santio.coffee.jda.annotations

/**
 * The description of the command.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Description(
    val value: String
)
