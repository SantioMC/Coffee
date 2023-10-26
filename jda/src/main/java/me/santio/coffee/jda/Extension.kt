package me.santio.coffee.jda

import me.santio.coffee.common.Coffee
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.requests.restaction.MessageEditAction
import java.util.function.Function

/**
 * This registers a global option to every single command for people
 * who have specific edge cases, these options will not be accessible through
 * the parameter list and will instead require a custom implementation of the
 * [CoffeeBundle#handleParameter] method
 * @param option The JDA option data
 * @return The Coffee object instance
 */
fun Coffee.registerGlobalOption(option: OptionData): Coffee {
    JDACommand.registerGlobalOption(option)
    return this
}

/**
 * This will unregister an already-defined global option
 * @param option The JDA option data
 * @return The Coffee object instance
 * @see registerGlobalOption
 */
fun Coffee.removeGlobalOption(option: OptionData): Coffee {
    JDACommand.removeGlobalOption(option)
    return this
}

/**
 * Removes a specific component id from a list
 * of action components.
 * @param id The id of the component to remove
 * @return A list of [LayoutComponent]
 */
fun List<LayoutComponent>.remove(id: String): List<LayoutComponent> {
    val copy = this.toMutableList()
    copy.forEach {
        it.actionComponents.removeIf { component ->
            component.id == id
        }
    }

    return copy
}

/**
 * Returns null if the component is empty
 * @param component The component to check
 * @return The component if it is not empty, null otherwise
 */
private fun nullIfEmpty(component: LayoutComponent): LayoutComponent? {
    return if (component.components.isEmpty()) null else component
}

/**
 * Modify the components of a message by passing a handler.
 * Returning null on the handling will delete the component, otherwise
 * any changes made in the returned value will be applied to the component.
 * @param handler The handler to apply to the components
 * @see ItemComponent
 */
fun Message.editComponent(handler: Function<ItemComponent, ItemComponent?>): MessageEditAction {
    return this.editMessageComponents(
        this.components.mapNotNull {
            val components = it.components.toList()

            it.components.clear()
            it.components.addAll(components.mapNotNull { component -> handler.apply(component) })

            it
        }.mapNotNull { nullIfEmpty(it) }
    )
}

/**
 * Modify the action components of a message by passing a handler.
 * See [editComponent] for modifying any kind of component attached to a message.
 * @param handler The handler to apply to the components
 * @return The message edit action
 * @see ItemComponent
 */
fun Message.editActionComponent(handler: Function<ActionComponent, ActionComponent?>): MessageEditAction {
    return this.editComponent {
        if (it !is ActionComponent) it
        else handler.apply(it)
    }
}