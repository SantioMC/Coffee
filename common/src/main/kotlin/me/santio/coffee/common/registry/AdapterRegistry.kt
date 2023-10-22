package me.santio.coffee.common.registry

import me.santio.coffee.common.Coffee
import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.impl.*
import me.santio.coffee.common.exception.NoAdapterException

/**
 * Handles all adapters registered in Coffee.
 */
object AdapterRegistry {

    private val adapters = mutableListOf<ArgumentAdapter<*>>(
        IntegerAdapter, StringAdapter, DoubleAdapter, FloatAdapter, LongAdapter, BooleanAdapter, ShortAdapter, ByteAdapter
    )

    /**
     * Registers a new argument adapter. This will be used to parse arguments
     * for the given type. If an adapter for the given type already exists,
     * it will be replaced with the new adapter. This change will apply immediately.
     * @param adapters The adapters to register.
     * @see ArgumentAdapter
     */
    @JvmStatic
    fun registerAdapter(vararg adapters: ArgumentAdapter<*>) {
        for (adapter in adapters) {
            this.adapters.removeIf { toBoxed(it.type) == toBoxed(adapter.type) }
            this.adapters.add(adapter)
        }
    }

    /**
     * Get the adapter for the given type.
     * @param type The type to get the adapter for.
     * @return The adapter for the given type.
     */
    @JvmStatic
    fun getAdapter(type: Class<*>, value: String? = null): ArgumentAdapter<*> {
        val adapter =  this.adapters.firstOrNull { toBoxed(it.type) == toBoxed(type) }

        if (adapter == null && Coffee.verbose) {
            println("""
            Failed to find adapter for: ${toBoxed(type).name}, the available options are:
            ${all().joinToString(", ") { toBoxed(it.type).name }}
            """.trimIndent())
        }

        return adapter ?: throw NoAdapterException("No adapter found for type ${toBoxed(type).name}, serialized value: $value")
    }

    /**
     * Get a list of all available adapters
     * @return A list of registered adapters
     */
    @JvmStatic
    fun all(): List<ArgumentAdapter<*>> {
        return this.adapters.toList()
    }

    /**
     * Converts a primitive type to it's boxed varient
     * @param primitive The primitive class
     * @return The boxed class type, if it isn't a primitive value itself will be returned.
     */
    @JvmStatic
    private fun toBoxed(primitive: Class<*>): Class<*> {
        if (!primitive.isPrimitive) return primitive
        return try {
            Class.forName("java.lang.${primitive.name}")
        } catch(e: ClassNotFoundException) { primitive }
    }

}