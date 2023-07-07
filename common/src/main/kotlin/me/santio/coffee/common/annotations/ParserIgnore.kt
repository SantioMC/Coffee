package me.santio.coffee.common.annotations

/**
 * Indicates that the annotated property should be ignored by the parser.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class ParserIgnore
