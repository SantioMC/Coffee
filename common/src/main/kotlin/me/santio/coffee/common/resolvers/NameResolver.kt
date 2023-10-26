package me.santio.coffee.common.resolvers

import me.santio.coffee.common.annotations.Command
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * This object is used to attempt to infer command names from various sources.
 */
@Suppress("MemberVisibilityCanBePrivate")
object NameResolver {

    /**
     * Attempt to resolve the name of a command from the class.
     * @param clazz The class to resolve the name from.
     * @param scope How far to search for the annotation.
     * @return The resolved name, or an automatically generated name.
     */
    @JvmStatic
    fun resolveName(clazz: Class<*>, scope: Scope): List<String> {
        val assignedName = AnnotationResolver.getAnnotation(
            clazz,
            Command::class.java,
            scope
        )

        return if (assignedName != null && assignedName.aliases.isNotEmpty()) assignedName.aliases.toList()
        else listOf(generateName(clazz))
    }

    /**
     * Attempt to resolve the name of a command from the method.
     * @param method The method to resolve the name from.
     * @return The resolved name, or an automatically generated name.
     */
    @JvmStatic
    fun resolveName(method: Method): List<String> {
        val assignedName = AnnotationResolver.getAnnotation(
            method,
            Command::class.java,
            Scope.SELF
        )

        return assignedName?.aliases?.toList() ?: listOf(generateName(method))
    }

    /**
     * Generate a name from the class.
     * @param clazz The class to generate the name from.
     * @return The generated name.
     */
    @JvmStatic
    fun generateName(clazz: Class<*>): String {
        val name = clazz.kotlin.simpleName ?: clazz.simpleName
        if (name.isEmpty()) return clazz.name.substring(clazz.name.indexOf('$')).lowercase()

        val firstCapital = name.substring(1)
            .indexOfFirst { it.isUpperCase() }
            .takeIf { it != -1 } ?: (name.length - 1)

        return name
            .lowercase()
            .removeSuffix("subcommand")
            .removeSuffix("command")
            .substring(0, firstCapital + 1)
    }

    /**
     * Generate a name from the method.
     * @param method The method to generate the name from.
     * @return The generated name.
     */
    @JvmStatic
    fun generateName(method: Method): String {
        return method.name
            .lowercase()
            .removeSuffix("subcommand")
            .removeSuffix("command")
    }

    /**
     * Generate a human-readable name for a field based on its name.
     * @param field The field to generate the name from.
     * @return The generated name.
     */
    fun generateName(field: Field): String {
        return field.name.mapIndexed { index, char ->
            if (index == 0) char.uppercase()
            else if (char.isUpperCase()) " $char"
            else char
        }.joinToString("")
    }

}