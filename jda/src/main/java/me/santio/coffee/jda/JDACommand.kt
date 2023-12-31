package me.santio.coffee.jda

import me.santio.coffee.common.Coffee
import me.santio.coffee.common.models.ResolvedParameter
import me.santio.coffee.common.models.tree.Bean
import me.santio.coffee.common.models.tree.CommandTree
import me.santio.coffee.common.models.tree.Group
import me.santio.coffee.common.registry.AdapterRegistry
import me.santio.coffee.common.resolvers.AnnotationResolver
import me.santio.coffee.common.resolvers.Scope
import me.santio.coffee.jda.annotations.Description
import me.santio.coffee.jda.annotations.Permission
import me.santio.coffee.jda.annotations.Skip
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

object JDACommand {

    private val globalOptions: MutableSet<OptionData> = mutableSetOf()

    private fun getOptionType(parameter: ResolvedParameter): OptionType {
        if (Coffee.isVerbose()) println("Finding option type for ${AdapterRegistry.toBoxed(parameter.type).name}")
        val type = when (AdapterRegistry.toBoxed(parameter.type).simpleName.lowercase()) {
            "int", "integer" -> OptionType.INTEGER
            "boolean" -> OptionType.BOOLEAN
            "user", "member" -> OptionType.USER
            "textchannel", "voicechannel", "guildchannel" -> OptionType.CHANNEL
            "double", "float" -> OptionType.NUMBER
            else -> OptionType.STRING
        }

        if (Coffee.isVerbose()) println("- Using OptionType.${type.name}")
        return type
    }

    private fun getDescription(parameter: ResolvedParameter): String {
        val description = parameter.parameter.java.getAnnotation(Description::class.java)
        return description?.value ?: "The ${parameter.name.lowercase()}"
    }

    private fun addOptions(command: SlashCommandData, bean: Bean) {
        for (parameter in bean.parameters.filter { it.type != SlashCommandInteractionEvent::class.java }) {
            val optionType = getOptionType(parameter)
            val adapter = AdapterRegistry.getAdapter(parameter.type)
            command.addOption(optionType, parameter.name, getDescription(parameter), !parameter.optional, adapter.hasSuggestions)
        }

        val skipGlobals = AnnotationResolver.getAnnotation(bean, Skip::class.java, Scope.ALL)
            ?.options
            ?.map { it.lowercase() }
            ?: emptyList()

        for (global in globalOptions) {
            if (skipGlobals.contains(global.name.lowercase())) continue
            if (command.options.any { it.name == global.name }) continue
            command.addOption(
                global.type,
                global.name,
                global.description,
                global.isRequired,
                global.isAutoComplete
            )
        }
    }

    private fun addOptions(command: SubcommandData, bean: Bean) {
        for (parameter in bean.parameters.filter { it.type != SlashCommandInteractionEvent::class.java }) {
            val optionType = getOptionType(parameter)
            command.addOption(optionType, parameter.name, getDescription(parameter), !parameter.optional)
        }

        val skipGlobals = AnnotationResolver.getAnnotation(bean, Skip::class.java, Scope.ALL)
            ?.options
            ?.map { it.lowercase() }
            ?: emptyList()

        for (global in globalOptions) {
            if (skipGlobals.contains(global.name.lowercase())) continue
            if (command.options.any { it.name == global.name }) continue
            command.addOption(
                global.type,
                global.name,
                global.description,
                global.isRequired,
                global.isAutoComplete
            )
        }
    }

    private fun createSubcommand(command: Bean): SubcommandData {
        val description = AnnotationResolver.getAnnotation(command, Description::class.java, Scope.PARENT)
        val subcommand = SubcommandData(command.name, description?.value ?: "No description provided")
        addOptions(subcommand, command)
        return subcommand
    }

    private fun attachSubcommands(slashCommand: SlashCommandData, command: CommandTree<*>) {
        for (leaf in command.children) {
            when (leaf) {
                is Group -> {
                    val subcommand = SubcommandGroupData(leaf.name, leaf.name)
                    for (child in leaf.children) if (child is Bean) {
                        subcommand.addSubcommands(createSubcommand(child))
                    }
                    slashCommand.addSubcommandGroups(subcommand)
                }
                is Bean -> slashCommand.addSubcommands(createSubcommand(leaf))
            }
        }
    }

    @JvmStatic
    fun register(bot: JDA, command: CommandTree<*>) {
        val permission = AnnotationResolver.getAnnotation(command, Permission::class.java, Scope.ALL)
        val description = AnnotationResolver.getAnnotation(command, Description::class.java, Scope.PARENT)

        val slashCommand = Commands.slash(command.name, description?.value ?: "No description provided")
        if (permission != null) slashCommand.setDefaultPermissions(DefaultMemberPermissions.enabledFor(*permission.value))

        // If the command tree has only one bean, we'll create a regular slash command, else we'll make sub commands
        val beans = command.all().values

        if (beans.size == 1) {
            val bean = beans.first()
            addOptions(slashCommand, bean)
        } else {
            attachSubcommands(slashCommand, command)
        }

        bot.upsertCommand(slashCommand).queue()
    }

    @JvmStatic
    fun registerGlobalOption(option: OptionData) {
        globalOptions.add(option)
    }

    @JvmStatic
    fun removeGlobalOption(option: OptionData) {
        globalOptions.remove(option)
    }

}