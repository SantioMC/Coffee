package me.santio.coffee.jda.gui.dropdown.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Option(
    val name: String = ""
)
