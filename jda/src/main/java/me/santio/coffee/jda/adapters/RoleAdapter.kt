package me.santio.coffee.jda.adapters

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.jda.JDAContextData
import net.dv8tion.jda.api.entities.Role

object RoleAdapter: ArgumentAdapter<Role>() {

    override val type: Class<Role> = Role::class.java

    @Suppress("NAME_SHADOWING")
    override fun adapt(arg: String, context: ContextData): Role? {
        val context = context as JDAContextData
        if (!arg.startsWith("<@&") || !arg.endsWith(">")) return null
        val id = arg.substring(3, arg.length - 1)
        return context.event.guild?.getRoleById(id)
    }

    override val error: String = "Failed to find the role '%arg%'"
}