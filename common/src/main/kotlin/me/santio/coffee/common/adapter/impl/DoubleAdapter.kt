package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData

object DoubleAdapter: ArgumentAdapter<Double>() {
    override val type: Class<Double> = Double::class.java

    override fun adapt(arg: String, context: ContextData): Double? {
        return arg.toDoubleOrNull()
    }
}