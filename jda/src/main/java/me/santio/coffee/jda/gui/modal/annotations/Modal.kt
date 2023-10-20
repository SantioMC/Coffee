package me.santio.coffee.jda.gui.modal.annotations

/**
 * Marks a class as a modal
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Modal(
 val title: String
)