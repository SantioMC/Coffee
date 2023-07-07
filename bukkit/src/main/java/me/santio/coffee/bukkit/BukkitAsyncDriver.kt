package me.santio.coffee.bukkit

import me.santio.coffee.common.async.AsyncDriver
import org.bukkit.plugin.java.JavaPlugin

class BukkitAsyncDriver(private val plugin: JavaPlugin): AsyncDriver() {
    override fun runAsync(runnable: Runnable) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, runnable)
    }

    override fun runSync(runnable: Runnable) {
        plugin.server.scheduler.runTask(plugin, runnable)
    }
}