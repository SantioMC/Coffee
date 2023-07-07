package me.santio.coffee.bukkit.annotations

/**
 * Gives a permission to a command, this will apply to all subcommands.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Permission(
    val value: String = "none"
)
