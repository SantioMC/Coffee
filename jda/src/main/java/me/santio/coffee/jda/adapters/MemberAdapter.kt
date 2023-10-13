package me.santio.coffee.jda.adapters

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.jda.JDAContextData
import net.dv8tion.jda.api.entities.Member

object MemberAdapter: ArgumentAdapter<Member>() {

    override val type: Class<Member> = Member::class.java

    @Suppress("NAME_SHADOWING")
    override fun adapt(arg: String, context: ContextData): Member? {
        val context = context as JDAContextData
        return arg.toLongOrNull()?.let {
            context.event.guild?.retrieveMemberById(it)?.complete()
        }
    }

    override val error: String = "Failed to find the member '%arg%'"
}