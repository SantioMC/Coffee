package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter

object DoubleAdapter: ArgumentAdapter<Double>() {
    override val type: Class<Double> = Double::class.java

    override fun adapt(arg: String): Double? {
        return arg.toDoubleOrNull()
    }
}