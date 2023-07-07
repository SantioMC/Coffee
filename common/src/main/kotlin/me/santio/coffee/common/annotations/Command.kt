package me.santio.coffee.common.annotations

/**
 * Marks a class as a command and allows it to be registered through Coffee.
 * If attached to a function, it will act as a way to rename or add aliases to the subcommand.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(vararg val aliases: String = [])
