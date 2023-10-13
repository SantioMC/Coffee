package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData

object FloatAdapter: ArgumentAdapter<Float>() {
    override val type: Class<Float> = Float::class.java

    override fun adapt(arg: String, context: ContextData): Float? {
        return arg.toFloatOrNull()
    }
}