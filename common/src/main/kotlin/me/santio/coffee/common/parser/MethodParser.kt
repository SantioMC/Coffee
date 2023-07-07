package me.santio.coffee.common.parser

import me.santio.coffee.common.annotations.Command
import me.santio.coffee.common.models.Path
import java.lang.reflect.Method

/**
 * Parses and evaluates methods to receive information about them.
 */
internal object MethodParser {

    /**
     * Checks if the method belongs to a [Command] class.
     */
    fun isValid(clazz: Class<*>): Boolean = ClassParser.isValid(clazz)

    /**
     * Gets the base class for this class
     * @return The base class or itself if it is the base class.
     */
    fun getBaseClass(clazz: Class<*>): Class<*> {
        var baseClass = clazz

        while (baseClass.declaringClass != null && isValid(baseClass.declaringClass)) {
            baseClass = baseClass.declaringClass
        }

        return baseClass
    }

    /**
     * Gets the path of the method.
     */
    @JvmStatic
    fun getPath(method: Method): Path {
        val clazz = method.declaringClass
        val path = Path.from(clazz)

        val isEntry = CommandParser.getEntryMethod(clazz) == method
        val annotation = method.getAnnotation(Command::class.java)
        var aliases = annotation?.aliases?.toList() ?: listOf(method.name)
        if (aliases.isEmpty()) aliases = listOf(method.name)

        if (!isEntry) path.sections.add(
            Path.Section(
            aliases.first(),
            aliases
        ))

        return path
    }

}