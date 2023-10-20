package me.santio.coffee.jda.gui.modal.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Placeholder(
    val value: String
)