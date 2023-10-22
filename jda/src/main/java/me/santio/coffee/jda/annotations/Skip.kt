package me.santio.coffee.jda.annotations

/**
 * Adds the ability for a bean to not include a global option
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Skip(
    vararg val options: String
)