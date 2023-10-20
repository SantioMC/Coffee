package me.santio.coffee.jda.gui.modal

import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.common.registry.AdapterRegistry
import me.santio.coffee.common.resolvers.AnnotationResolver
import me.santio.coffee.common.resolvers.IDResolver
import me.santio.coffee.jda.gui.modal.annotations.Item
import me.santio.coffee.jda.gui.modal.annotations.Modal
import me.santio.coffee.jda.gui.modal.annotations.Placeholder
import me.santio.coffee.jda.gui.modal.exceptions.ModalAdaptException
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInput
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.concurrent.schedule
import me.santio.coffee.jda.gui.modal.ExecutableModal as ExecutableModal1
import net.dv8tion.jda.api.interactions.modals.Modal as JDAModal

object ModalManager {
    
    private val modals = mutableMapOf<String, ExecutableModal1<*>>()
    
    private fun createActionRow(field: Field): ActionRow? {
        if (Modifier.isPrivate(field.modifiers)) return null
        val item = AnnotationResolver.getAnnotation(field, Item::class.java) ?: return null
        val placeholder = AnnotationResolver.getAnnotation(field, Placeholder::class.java)?.value ?: ""
        field.isAccessible = true
        
        val input = TextInput.create(field.name, item.label, item.type.inputStyle)
        if (placeholder.isNotEmpty()) input.setPlaceholder(placeholder)
        
        if (item.minLength != -1) input.minLength = item.minLength
        if (item.maxLength != -1) input.maxLength = item.maxLength
        
        return ActionRow.of(input.build())
    }
    
    private fun buildModal(instance: Any): ExecutableModal1<*> {
        val modal = AnnotationResolver.getAnnotation(instance::class.java, Modal::class.java)
                ?: throw IllegalStateException("The modal you tried opening: '${instance::class.java.name}' is not annotated by @Modal!")
        
        val fields = instance::class.java.declaredFields.mapNotNull {
            createActionRow(it)
        }
        
        val id = IDResolver.id()
        val jdaModal = JDAModal.create(id, modal.title)
            .addComponents(*fields.toTypedArray())
            .build()
        
        modals[id] = ExecutableModal1(instance, jdaModal)
        Timer().schedule(1000 * 60 * 10) {
            remove(id)
        }
        
        return modals[id]!!
    }
    
    /**
     * Unregister and remove an existing modal from the manager
     * @param modalId The modal id to remove
     */
    fun remove(modalId: String) {
        modals.remove(modalId)
    }
    
    /**
     * Show a modal to a user and attach a callback
     * @param action The action to reply to with the embed
     * @param modal The instance of the modal
     * @param callback The callback which will be executed when the modal is submitted
     */
    @SuppressWarnings("UNCHECKED_CAST")
    fun <T: Any>showModal(action: IModalCallback, modal: T, callback: BiConsumer<T, ModalInteractionEvent>) {
        val jdaModal = buildModal(modal) as ExecutableModal1<T>
        action.replyModal(jdaModal.jda).queue()
        jdaModal.executable = callback
    }

    /**
     * Show a modal to a user and attach a callback
     * @param action The action to reply to with the embed
     * @param modal The class of the modal
     * @param callback The callback which will be executed when the modal is submitted
     */
    fun <T: Any>showModal(action: IModalCallback, modal: Class<T>, callback: BiConsumer<T, ModalInteractionEvent>) {
        showModal(action, modal.getDeclaredConstructor().newInstance(), callback)
    }
    
    /**
     * Submit a response for a open modal
     * @param modalId The id of the modal
     * @param event The triggered event
     * @throws ModalAdaptException If a adapter failed to parse a specific field
     */
    fun respond(modalId: String, event: ModalInteractionEvent) {
        val modal = modals[modalId] ?: return
        val fields = modal.modal::class.java.declaredFields.filter {
            AnnotationResolver.hasAnnotation(it, Item::class.java)
        }
        
        for (field in fields) {
            val adapter = AdapterRegistry.getAdapter(field.type, field.name)
            val arg = event.getValue(field.name)?.asString
            
            if (!adapter.isValid(
                arg ?: throw IllegalStateException("Failed to find associated option from field: '${field.name}'"),
                ContextData(null)
            )) throw ModalAdaptException(adapter.error.replace("%arg%", arg))

            field.set(modal.modal, adapter.adapt(
                arg,
                ContextData(null)
            ))
        }
        
        modals[modalId]?.execute(event)
    }
    
}