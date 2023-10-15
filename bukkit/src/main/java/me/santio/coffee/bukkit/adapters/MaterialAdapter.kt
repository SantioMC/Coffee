package me.santio.coffee.bukkit.adapters

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData
import org.bukkit.Material

object MaterialAdapter: ArgumentAdapter<Material>() {
    override val type: Class<Material> = Material::class.java

    override fun adapt(arg: String, context: ContextData): Material? {
        return Material.getMaterial(arg.uppercase().replace(" ", "_"))
    }

    override val error: String = "The material '%arg%' was not found"
}