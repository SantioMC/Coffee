package me.santio.coffee.jda.gui.modal

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.modals.Modal
import java.util.function.BiConsumer
import java.util.function.Consumer

data class ExecutableModal<T: Any>(
    val modal: T,
    val jda: Modal,
    var executable: BiConsumer<T, ModalInteractionEvent> = BiConsumer { _, _ -> }
) {
    val id: String
        get() = jda.id
    
    fun execute(event: ModalInteractionEvent) {
        ModalManager.remove(jda.id)
        executable.accept(modal, event)   
    }
}