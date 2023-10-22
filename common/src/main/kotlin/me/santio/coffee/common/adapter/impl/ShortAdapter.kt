package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData

object ShortAdapter: ArgumentAdapter<Short>() {
    override val type: Class<Short> = Short::class.java

    override fun adapt(arg: String, context: ContextData): Short? {
        return arg.toShortOrNull()
    }
}