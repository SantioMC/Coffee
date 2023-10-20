package me.santio.coffee.jda.gui.modal.exceptions

class ModalAdaptException(override val message: String? = "Failed to adapt a field"): Exception(message)