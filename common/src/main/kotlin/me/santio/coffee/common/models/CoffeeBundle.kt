package me.santio.coffee.common.models

import me.santio.coffee.common.Coffee
import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.async.AsyncDriver
import me.santio.coffee.common.async.DefaultAsyncDriver
import me.santio.coffee.common.parameter.ParameterContext

/**
 * The base class for a Coffee bundle, this is used to import a specific implementation of Coffee into your project.
 * @see Coffee.import
 */
abstract class CoffeeBundle {
    open val adapters: List<ArgumentAdapter<*>> = emptyList()
    open val asyncDriver: AsyncDriver = DefaultAsyncDriver

    /**
     * Called when a parameter is being handled, you can add your own logic here.
     * If the context is left unresponded, the default behaviour will be used.
     * @param context The context of the parameter.
     * @see ParameterContext
     */
    open fun handleParameter(context: ParameterContext<*>) {}

    /**
     * Called when the bundle is ready to register to listeners.
     */
    open fun ready() {}

    fun import() {
        Coffee.import(this)
    }
}