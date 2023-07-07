package me.santio.coffee.bukkit.builders

import me.santio.coffee.bukkit.annotations.Description
import me.santio.coffee.common.models.Path
import me.santio.coffee.common.models.SubCommand

data class CommandBuilder(
    val name: String,
    val namespace: String = "coffee"
) {

    companion object {
        @JvmStatic
        fun from(command: SubCommand): CommandBuilder {
            val path = Path.from(command)
            val name = path.sections.first().name
            val aliases = path.sections.firstOrNull()?.aliases?.minus(name) ?: listOf()
            val description = command.getBaseClass().getAnnotation(Description::class.java)?.value
                ?: "No command description provided"

            return CommandBuilder(name)
                .aliases(*aliases.toTypedArray())
                .async(command.isAsync)
                .description(description)
        }
    }

    private var description: String = "No command description provided"
    private var aliases: List<String> = listOf()
    private var async: Boolean = false

    fun description(description: String): CommandBuilder {
        this.description = description
        return this
    }

    fun aliases(vararg aliases: String): CommandBuilder {
        this.aliases = aliases.toList()
        return this
    }

    fun async(async: Boolean): CommandBuilder {
        this.async = async
        return this
    }

    fun description(): String = description
    fun aliases(): List<String> = aliases
    fun async(): Boolean = async

}