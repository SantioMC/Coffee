package me.santio.coffee.common.registry

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.impl.*
import me.santio.coffee.common.exception.NoAdapterException

/**
 * Handles all adapters registered in Coffee.
 */
object AdapterRegistry {

    private val adapters = mutableListOf<ArgumentAdapter<*>>(
        IntegerAdapter, StringAdapter, DoubleAdapter, FloatAdapter, LongAdapter, BooleanAdapter
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
            this.adapters.removeIf { it.type == adapter.type }
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
        // Booleans have a weird edge case where they differ between Java and Kotlin
        if (type.name == "java.lang.Boolean") return BooleanAdapter

        return this.adapters.firstOrNull { it.type == type }
            ?: throw NoAdapterException("No adapter found for type ${type.name}, serialized value: $value")
    }

}