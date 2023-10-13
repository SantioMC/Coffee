package me.santio.coffee.jda.annotations

import net.dv8tion.jda.api.Permission

/**
 * The permission of the command.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Permission(
    vararg val value: Permission
)
