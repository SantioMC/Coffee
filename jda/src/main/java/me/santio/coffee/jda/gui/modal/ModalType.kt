package me.santio.coffee.jda.gui.modal

import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

enum class ModalType(
    val inputStyle: TextInputStyle
) {
    SHORT_TEXT(TextInputStyle.SHORT),
    PARAGRAPH_TEXT(TextInputStyle.PARAGRAPH),
    ;
}