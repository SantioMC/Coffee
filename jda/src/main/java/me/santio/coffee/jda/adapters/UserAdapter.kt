package me.santio.coffee.jda.adapters

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData
import me.santio.coffee.jda.JDAContextData
import net.dv8tion.jda.api.entities.User

object UserAdapter: ArgumentAdapter<User>() {

    override val type: Class<User> = User::class.java

    @Suppress("NAME_SHADOWING")
    override fun adapt(arg: String, context: ContextData): User? {
        val context = context as JDAContextData
        return arg.toLongOrNull()?.let {
            context.bot.retrieveUserById(it).complete()
        }
    }

    override val error: String = "Failed to find the user '%arg%'"
}