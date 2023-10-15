package me.santio.coffee.common.annotations

/**
 * Flags a function as the entry point of a command, if there happens to be multiple, it'll choose
 * the first one that programmatically shows up.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Entry
