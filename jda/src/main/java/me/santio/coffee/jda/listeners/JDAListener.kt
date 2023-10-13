package me.santio.coffee.jda.listeners

import me.santio.coffee.common.Coffee
import me.santio.coffee.common.exception.CommandErrorException
import me.santio.coffee.common.models.Path
import me.santio.coffee.jda.JDAContextData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class JDAListener(private val bot: JDA): ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {

        val options = event.options.map {
            it.asString
        }

        val path = Path.from(event.name)

        try {
            Coffee.execute(path, options, JDAContextData(event, bot))
        } catch(e: CommandErrorException) {
            event.replyEmbeds(
                EmbedBuilder()
                    .setTitle(" ")
                    .setDescription("""
                        An error occurred while executing the command
                        ```diff
                        - ${e.message}
                        ```
                    """.trimIndent())
                    .setColor(0x67060c)
                    .build()
            ).setEphemeral(true).queue()
        }

    }

}