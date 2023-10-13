package me.santio.coffee.jda.adapters

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.jda.JDAContextData
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel

object VoiceChannelAdapter: ArgumentAdapter<VoiceChannel>() {

    override val type: Class<VoiceChannel> = VoiceChannel::class.java

    @Suppress("NAME_SHADOWING")
    override fun adapt(arg: String, context: ContextData): VoiceChannel? {
        val context = context as JDAContextData
        val guild = context.event.guild ?: return null

        return arg.toLongOrNull()?.let {
            guild.getVoiceChannelById(it)
        }
    }

    override val error: String = "Failed to find the voice channel '%arg%'"
}