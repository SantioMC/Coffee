package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData

object NumberAdapter: ArgumentAdapter<Number>() {
    override val type: Class<Number> = Number::class.java

    override fun adapt(arg: String, context: ContextData): Number? {
        return arg.toDoubleOrNull()
    }
}