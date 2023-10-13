package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData

object StringAdapter: ArgumentAdapter<String>() {
    override val type: Class<String> = String::class.java
    override fun adapt(arg: String, context: ContextData): String = arg
}