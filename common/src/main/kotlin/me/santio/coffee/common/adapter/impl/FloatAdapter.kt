package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter

object FloatAdapter: ArgumentAdapter<Float>() {
    override val type: Class<Float> = Float::class.java

    override fun adapt(arg: String): Float? {
        return arg.toFloatOrNull()
    }
}