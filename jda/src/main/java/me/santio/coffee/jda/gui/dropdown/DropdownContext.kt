package me.santio.coffee.jda.gui.dropdown

import me.santio.coffee.jda.editActionComponent
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

@Suppress("MemberVisibilityCanBePrivate")
open class DropdownContext<T: Any>(
    val dropdown: Dropdown<*>,
    val event: SelectMenuInteraction<*, *>,
) {
    lateinit var selected: T
        internal set

    /**
     * @return true if the dropdown is a string menu
     */
    fun isStringMenu(): Boolean {
        return event is StringSelectInteraction
    }

    /**
     * @return true if the dropdown is an entity menu
     */
    fun isEntityMenu() : Boolean {
        return event is EntitySelectMenu
    }

    /**
     * Enables the dropdown
     * @return This context
     * @see Dropdown.disabled
     */
    fun enable(): DropdownContext<T> {
        event.message.editActionComponent {
            if (it.id == dropdown.id) dropdown.disable(false).build()
            else it
        }.queue()

        DropdownManager.dropdowns.add(dropdown)
        return this
    }

    /**
     * Disables the dropdown
     * @return This context
     * @see Dropdown.disabled
     */
    fun disable(): DropdownContext<T> {
        event.message.editActionComponent {
            if (it.id == dropdown.id) dropdown.disable().build()
            else it
        }.queue()

        DropdownManager.unregister(dropdown.id)
        return this
    }

    /**
     * Completely delete and unregister the dropdown
     * @return This context
     */
    fun remove(): DropdownContext<T> {
        DropdownManager.unregister(dropdown.id)

        event.message.editActionComponent {
            if (it.id == dropdown.id) null
            else it
        }.queue()

        return this
    }

}
