package me.santio.coffee.bukkit.adapters

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PlayerAdapter: ArgumentAdapter<Player>() {
    override val type: Class<Player> = Player::class.java

    override fun adapt(arg: String, context: ContextData): Player? {
        return Bukkit.getPlayer(arg)
    }

    override val error: String = "The player '%arg%' was not found"
}