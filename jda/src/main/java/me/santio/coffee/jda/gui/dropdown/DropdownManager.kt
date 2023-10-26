package me.santio.coffee.jda.gui.dropdown

object DropdownManager {

    internal val dropdowns = mutableListOf<Dropdown<*>>()

    fun unregister(id: String) {
        dropdowns.removeIf {
            it.id == id
        }
    }

    fun get(id: String): Dropdown<*>? {
        return dropdowns.firstOrNull {
            it.id == id
        }
    }

    fun clear() {
        dropdowns.clear()
    }

}
