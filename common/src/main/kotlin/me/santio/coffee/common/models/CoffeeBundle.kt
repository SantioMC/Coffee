package me.santio.coffee.common.models

import me.santio.coffee.common.Coffee
import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.async.AsyncDriver
import me.santio.coffee.common.async.DefaultAsyncDriver
import me.santio.coffee.common.parameter.AutomaticParameter

abstract class CoffeeBundle {
    abstract val automaticParameters: List<AutomaticParameter>
    abstract val adapters: List<ArgumentAdapter<*>>
    open val asyncDriver: AsyncDriver = DefaultAsyncDriver

    fun import() {
        Coffee.import(this)
    }
}