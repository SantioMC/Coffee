package me.santio.coffee.bukkit

import me.santio.coffee.common.parameter.AutomaticParameter
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

object SenderAutomaticParameter: AutomaticParameter() {

    const val INTERNAL_CONSOLE = "COFFEE_INTERNAL_CONSOLE"

    override val types: List<Class<*>>
        get() = listOf(Player::class.java, CommandSender::class.java, ConsoleCommandSender::class.java)

    override fun handle(type: Class<*>, input: String): Any {
        return when (type) {
            Player::class.java -> Bukkit.getPlayer(input) ?: throw IllegalArgumentException("Player $input not found")
            CommandSender::class.java -> if (input == INTERNAL_CONSOLE) Bukkit.getConsoleSender() else Bukkit.getPlayer(input) ?: throw IllegalArgumentException("Player $input not found")
            ConsoleCommandSender::class.java -> Bukkit.getConsoleSender()
            else -> throw IllegalArgumentException("Invalid type $type")
        }
    }

}