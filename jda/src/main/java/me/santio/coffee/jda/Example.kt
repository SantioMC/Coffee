//package me.santio.coffee.jda
//
//import me.santio.coffee.jda.gui.dropdown.Dropdown
//import net.dv8tion.jda.api.entities.Message
//
//object Example {
//
//    val logMessageChanges: Boolean = true // Log Message Changes
//
//    @JvmStatic
//    fun main(args: Array<String>) {
//
//        val event: Message = null!!
//
//        event.reply("Hello World!")
//            .addActionRow(
//                Dropdown.from(Example::class.java) {
//                    it.selected.logMessageChanges
//                }.build()
//            )
//
//    }
//
//}