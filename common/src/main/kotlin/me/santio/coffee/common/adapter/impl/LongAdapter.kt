package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData

object LongAdapter: ArgumentAdapter<Long>() {
    override val type: Class<Long> = Long::class.java

    override fun adapt(arg: String, context: ContextData): Long? {
        return arg.toLongOrNull()
    }
}