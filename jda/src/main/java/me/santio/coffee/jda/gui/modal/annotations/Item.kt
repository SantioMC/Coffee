package me.santio.coffee.jda.gui.modal.annotations

import me.santio.coffee.jda.gui.modal.ModalType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Item(
    val label: String,
    val type: ModalType = ModalType.SHORT_TEXT,
    
    val minLength: Int = -1,
    val maxLength: Int = -1
)