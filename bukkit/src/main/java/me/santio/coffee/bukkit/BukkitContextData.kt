package me.santio.coffee.bukkit

import me.santio.coffee.common.adapter.ContextData
import org.bukkit.command.CommandSender

/**
 * Additional data for all parameter contexts.
 */
data class BukkitContextData(
    val sender: CommandSender
): ContextData()