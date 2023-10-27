package me.santio.coffee.jda.gui.dropdown.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class Option(
    val name: String = "",
    val emoji: String = ""
)
