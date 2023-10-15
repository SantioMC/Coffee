package me.santio.coffee.bukkit.builders

import me.santio.coffee.bukkit.annotations.Description
import me.santio.coffee.common.models.tree.CommandTree
import me.santio.coffee.common.resolvers.AnnotationResolver
import me.santio.coffee.common.resolvers.Scope

data class CommandBuilder(
    val name: String,
    val namespace: String = "coffee"
) {

    companion object {
        @JvmStatic
        fun from(command: CommandTree<*>): CommandBuilder {
            val name = command.name
            val aliases = command.aliases

            val description = AnnotationResolver.getAnnotation(command, Description::class.java, Scope.PARENT)?.value
                ?: "No command description provided"

            return CommandBuilder(name)
                .aliases(*aliases.toTypedArray())
                .description(description)
        }
    }

    private var description: String = "No command description provided"
    private var aliases: List<String> = listOf()

    fun description(description: String): CommandBuilder {
        this.description = description
        return this
    }

    fun aliases(vararg aliases: String): CommandBuilder {
        this.aliases = aliases.toList()
        return this
    }

    fun description(): String = description
    fun aliases(): List<String> = aliases

}