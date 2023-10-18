package me.santio.coffee.jda

import me.santio.coffee.common.models.ResolvedParameter
import me.santio.coffee.common.models.tree.Bean
import me.santio.coffee.common.models.tree.CommandTree
import me.santio.coffee.common.models.tree.Group
import me.santio.coffee.common.registry.AdapterRegistry
import me.santio.coffee.common.resolvers.AnnotationResolver
import me.santio.coffee.common.resolvers.Scope
import me.santio.coffee.jda.annotations.Description
import me.santio.coffee.jda.annotations.Permission
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
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

object JDACommand {

    private fun getOptionType(parameter: ResolvedParameter): OptionType {
        return when (parameter.type) {
            Int::class.java -> OptionType.INTEGER
            Boolean::class.java -> OptionType.BOOLEAN
            User::class.java, Member::class.java -> OptionType.USER
            TextChannel::class.java, VoiceChannel::class.java, GuildChannel::class.java -> OptionType.CHANNEL
            Double::class.java, Float::class.java -> OptionType.NUMBER
            else -> OptionType.STRING
        }
    }

    private fun getDescription(parameter: ResolvedParameter): String {
        val description = parameter.parameter.java.getAnnotation(Description::class.java)
        return description?.value ?: "The ${parameter.name.lowercase()}"
    }

    private fun addOptions(command: SlashCommandData, bean: Bean) {
        for (parameter in bean.parameters.filter { it.type != SlashCommandInteractionEvent::class.java }) {
            val optionType = getOptionType(parameter)
            val adapter = AdapterRegistry.getAdapter(parameter.type)
            command.addOption(optionType, parameter.name, getDescription(parameter), !parameter.optional, adapter.hasSuggestions())
        }
    }

    private fun addOptions(command: SubcommandData, bean: Bean) {
        for (parameter in bean.parameters.filter { it.type != SlashCommandInteractionEvent::class.java }) {
            val optionType = getOptionType(parameter)
            command.addOption(optionType, parameter.name, getDescription(parameter), !parameter.optional)
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

    fun register(bot: JDA, command: CommandTree<*>) {
        val permission = AnnotationResolver.getAnnotation(command, Permission::class.java, Scope.SELF)
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

}