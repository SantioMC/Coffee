package me.santio.coffee.common.parser

import me.santio.coffee.common.annotations.Command
import me.santio.coffee.common.models.Path

/**
 * Parses and evaluates classes annotated with [Command] to receive their information.
 */
internal object ClassParser {

    /**
     * Checks if the class is annotated with [Command].
     */
    fun isValid(clazz: Class<*>): Boolean {
        var current = clazz
        var annotation: Command? = current.getAnnotation(Command::class.java)

        while (clazz.declaringClass != null && annotation == null) {
            current = clazz.declaringClass
            annotation = current.getAnnotation(Command::class.java)

            if (annotation != null) return true
        }

        return annotation != null
    }

    /**
     * Gets the effective name of the class.
     * @return Either the annotation details or calculate it from the class name.
     */
    fun getEffectiveName(clazz: Class<*>): List<String> {
        val annotation = clazz.getAnnotation(Command::class.java)
        val aliases = annotation?.aliases?.toList() ?: emptyList()

        val firstCapital = clazz.simpleName.substring(1)
            .indexOfFirst { it.isUpperCase() }
            .takeIf { it != -1 } ?: (clazz.simpleName.length - 1)

        val name = clazz.simpleName
            .lowercase()
            .removeSuffix("command")
            .removeSuffix("subcommand")
            .substring(0, firstCapital + 1)

        return aliases + name
    }

    /**
     * Get all parent classes until we reach the base class.
     * @return A list of all parent classes.
     */
    private fun getParents(clazz: Class<*>): List<Class<*>> {
        val parents = mutableListOf(clazz)
        var current = clazz

        while (current.declaringClass != null) {
            current = current.declaringClass
            parents.add(current)
        }

        return parents.reversed().filter { isValid(it) }
    }

    /**
     * Gets the base class for this class
     * @return The base class or itself if it is the base class.
     */
    fun getBaseClass(clazz: Class<*>): Class<*> {
        return getParents(clazz).firstOrNull() ?: clazz
    }

    /**
     * Gets the command path of the class.
     * @return The command path.
     */
    fun getPath(clazz: Class<*>): Path {
        val classes = getParents(clazz)
        val sections = mutableListOf<Path.Section>()

        for (c in classes) {
            val name = getEffectiveName(c)

            sections.add(Path.Section(name.first(), name))
        }

        return Path(sections)
    }

}