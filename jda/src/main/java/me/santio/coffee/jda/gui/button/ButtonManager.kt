package me.santio.coffee.jda.gui.button

@Suppress("unused")
object ButtonManager {

    internal val buttons = mutableListOf<Button>()

    fun unregister(id: String) {
        buttons.removeIf {
            it.id == id
        }
    }

    fun clear() {
        buttons.clear()
    }

}