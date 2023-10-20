package me.santio.coffee.common.adapter

/**
 * An adapter to convert a string to a specific type.
 * Example: String -> Integer
 */
abstract class ArgumentAdapter<T> {

    /**
     * The type of the argument.
     * Example: Integer::class.java
     */
    abstract val type: Class<T>

    /**
     * The error message to send if the argument is invalid.
     */
    open val error: String = "Invalid argument: %s"

    /**
     * If you want to implement a custom suggestion handler, override both this and [suggest].
     * Set this to true if you want to handle suggestions yourself.
     */
    open val hasSuggestions: Boolean = false

    /**
     * The function to check if the string should be converted using this adapter.
     * By default, we check if the argument is not null, however you can override this to change it's behaviour
     * @param arg The argument to check.
     * @return True if the argument should be converted using this adapter.
     */
    open fun <C: ContextData>isValid(arg: String, context: C): Boolean = adapt(arg, context) != null

    /**
     * The function that gets ran whenever a suggestion is requested for this adapter.
     * By default, we return an empty list, however you can override this to change its behaviour
     *
     * @param arg What the user has typed so far.
     * @return A list of suggestions, these will be filtered automatically by Coffee for you.
     */
    open fun suggest(arg: String): List<String> = emptyList()

    /**
     * The function to convert the argument to the type.
     * Example: Bukkit.getPlayerExact(it)
     *
     * @param arg The argument to convert.
     * @return The converted argument. If null, an error message will be sent and the command execution will stop
     */
    abstract fun adapt(arg: String, context: ContextData): T?

}
