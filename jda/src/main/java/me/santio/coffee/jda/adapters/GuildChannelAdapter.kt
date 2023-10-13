package me.santio.coffee.jda.adapters

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.jda.JDAContextData
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel

object GuildChannelAdapter: ArgumentAdapter<GuildChannel>() {

    override val type: Class<GuildChannel> = GuildChannel::class.java

    @Suppress("NAME_SHADOWING")
    override fun adapt(arg: String, context: ContextData): GuildChannel? {
        val context = context as JDAContextData
        val guild = context.event.guild ?: return null

        return arg.toLongOrNull()?.let {
            guild.getGuildChannelById(it)
        }
    }

    override val error: String = "Failed to find the channel '%arg%'"
}