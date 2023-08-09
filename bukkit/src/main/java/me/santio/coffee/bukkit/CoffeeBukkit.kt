package me.santio.coffee.bukkit

import me.santio.coffee.bukkit.adapters.PlayerAdapter
import me.santio.coffee.bukkit.utils.BukkitUtils
import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.async.AsyncDriver
import me.santio.coffee.common.models.CoffeeBundle
import me.santio.coffee.common.parameter.AutomaticParameter
import me.santio.coffee.common.parser.CommandParser
import org.bukkit.plugin.java.JavaPlugin

class CoffeeBukkit(plugin: JavaPlugin): CoffeeBundle() {
    override val automaticParameters: List<AutomaticParameter> = listOf(SenderAutomaticParameter)
    override val adapters: List<ArgumentAdapter<*>> = listOf(PlayerAdapter)
    override val asyncDriver: AsyncDriver = BukkitAsyncDriver(plugin)

    init {
        CommandParser.onRegister { it.forEach(BukkitUtils::register) }
    }
}