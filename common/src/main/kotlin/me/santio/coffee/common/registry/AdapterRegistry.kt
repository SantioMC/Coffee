package me.santio.coffee.common.registry

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
        return this.adapters.firstOrNull { toBoxed(it.type) == toBoxed(type) }
            ?: throw NoAdapterException("No adapter found for type ${type.name}, serialized value: $value")
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
        return when (primitive) {
            Boolean::class.javaPrimitiveType -> Boolean::class.java
            Char::class.javaPrimitiveType -> Char::class.java
            Byte::class.javaPrimitiveType -> Byte::class.java
            Short::class.javaPrimitiveType -> Short::class.java
            Int::class.javaPrimitiveType -> Int::class.java
            Long::class.javaPrimitiveType -> Long::class.java
            Float::class.javaPrimitiveType -> Float::class.java
            Double::class.javaPrimitiveType -> Double::class.java
            Void::class.javaPrimitiveType -> Void::class.java
            else -> primitive
        }
    }

}