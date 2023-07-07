package me.santio.coffee.common.annotations

/**
 * Without any implementations, all commands will be executed synchronously.
 * However, if there is a valid implementation that has support for asynchronous execution,
 * this annotation can be used to mark the command to ignore that and run synchronously.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Sync
