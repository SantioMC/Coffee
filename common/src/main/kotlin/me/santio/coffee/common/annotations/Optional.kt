package me.santio.coffee.common.annotations

/**
 * Marks an argument as optional, in Kotlin this can be excluded and instead opt in
 * to use the default nullable operator (?) to achieve the same effect.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Optional
