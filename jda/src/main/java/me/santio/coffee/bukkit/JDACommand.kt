package me.santio.coffee.bukkit

import me.santio.coffee.bukkit.annotations.Description
import me.santio.coffee.bukkit.annotations.Permission
import me.santio.coffee.common.models.CommandParameter
import me.santio.coffee.common.models.Path
import me.santio.coffee.common.models.SubCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import java.io.File

object JDACommand {

    private fun getOptionType(parameter: CommandParameter): OptionType {
        return when (parameter.type) {
            Int::class.java -> OptionType.INTEGER
            Boolean::class.java -> OptionType.BOOLEAN
            User::class.java -> OptionType.USER
            Member::class.java -> OptionType.USER
            File::class.java -> OptionType.ATTACHMENT
            TextChannel::class.java, VoiceChannel::class.java, GuildChannel::class.java -> OptionType.CHANNEL
            else -> OptionType.STRING
        }
    }

    private fun getDescription(parameter: CommandParameter): String {
        val description = parameter.parameter.getAnnotation(Description::class.java)
        return description?.value ?: "The ${parameter.name.lowercase()}"
    }

    fun register(bot: JDA, command: SubCommand) {
        val path = Path.from(command)
        val permission = command.method.getAnnotation(Permission::class.java)
        val description = command.method.getAnnotation(Description::class.java)

        val slashCommand = Commands.slash(path.sections.first().name, description?.value ?: "No description provided")
        if (permission != null) slashCommand.setDefaultPermissions(DefaultMemberPermissions.enabledFor(*permission.value))

        for (parameter in command.parameters) {
            val optionType = getOptionType(parameter)
            slashCommand.addOption(optionType, parameter.name, getDescription(parameter))
        }

        bot.upsertCommand(slashCommand).queue()
    }

}