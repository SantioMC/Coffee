package me.santio.coffee.bukkit.annotations

/**
 * Marks the parameter as the sender of the command, this will make Coffee inject
 * the sender into the parameter.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Sender