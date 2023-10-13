package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData

object IntegerAdapter: ArgumentAdapter<Int>() {
    override val type: Class<Int> = Int::class.java

    override fun adapt(arg: String, context: ContextData): Int? {
        return arg.toIntOrNull()
    }
}