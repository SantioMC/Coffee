package me.santio.coffee.common.parameter

abstract class AutomaticParameter {

    /**
     * The types that this parameter can accept.
     */
    abstract val types: List<Class<*>>

    /**
     * Handles this parameter based on the type.
     * @param type The type that the command uses, it will be one of the types in [types].
     * @return An instance of the type that the command uses. Make sure that the return type
     *       matches the type that the parameter is passed in.
     */
    abstract fun handle(type: Class<*>, input: String): Any

}