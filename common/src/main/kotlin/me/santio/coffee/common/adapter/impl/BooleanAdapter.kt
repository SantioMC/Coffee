package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData

object BooleanAdapter: ArgumentAdapter<Boolean>() {
    override val type: Class<Boolean> = Boolean::class.java

    override fun adapt(arg: String, context: ContextData): Boolean? {
        return when (arg.lowercase()) {
            in listOf("yes", "true", "1", "on") -> true
            in listOf("no", "false", "0", "off") -> false
            else -> null
        } 
    }
}