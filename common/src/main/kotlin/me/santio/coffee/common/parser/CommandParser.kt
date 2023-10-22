package me.santio.coffee.common.parser

import me.santio.coffee.common.annotations.Command
import me.santio.coffee.common.annotations.ParserIgnore
import me.santio.coffee.common.annotations.Sync
import me.santio.coffee.common.exception.CommandValidationException
import me.santio.coffee.common.models.tree.Bean
import me.santio.coffee.common.models.tree.CommandTree
import me.santio.coffee.common.models.tree.Group
import me.santio.coffee.common.models.tree.Leaf
import me.santio.coffee.common.resolvers.AnnotationResolver
import me.santio.coffee.common.resolvers.NameResolver
import me.santio.coffee.common.resolvers.Scope
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.kotlinFunction

/**
 * The main command parser, the primary task of this class is to generate a command tree from a class.
 */
@Suppress("MemberVisibilityCanBePrivate")
object CommandParser {

    /**
     * Creates an instance of a class, or gets the object instance if it is a singleton.
     */
    @JvmStatic
    fun <T: Any> getInstance(clazz: Class<T>): T {
        return try {
            clazz.kotlin.objectInstance ?: run {
                clazz.getDeclaredConstructor().isAccessible = true
                clazz.getDeclaredConstructor().newInstance()
            }
        } catch(e: IllegalAccessException) {
            throw CommandValidationException("Failed to create instance of class ${clazz.name}")
        }
    }

    /**
     * Parses a class into a command tree.
     */
    @JvmStatic
    fun <T: Any> parse(clazz: Class<T>): CommandTree<T> {
        return parse(getInstance(clazz))
    }

    /**
     * Parses an instance of a class into a command tree.
     */
    @JvmStatic
    fun <T: Any> parse(instance: T): CommandTree<T> {
        val clazz = instance::class.java

        if (!isValid(clazz))
            throw CommandValidationException("Class ${clazz.name} is not a command, annotate it with @Command")

        val tree = CommandTree(NameResolver.resolveName(clazz, Scope.SELF), instance)
        tree.children.addAll(getBeans(clazz, tree.name, instance))

        return tree
    }

    /**
     * Gets all coffee beans from a class.
     * @param clazz The class to get the coffee beans from.
     * @return The coffee beans.
     */
    @JvmStatic
    @JvmOverloads
    fun getBeans(clazz: Class<*>, path: String, instance: Any? = null): MutableList<Leaf> {
        val leaves = mutableListOf<Leaf>()

        // Add all valid methods
        leaves += clazz.declaredMethods
            .filter { isValid(it) }
            .map { createBean(
                instance ?: run {
                    val constructor = clazz.getDeclaredConstructor()
                    constructor.isAccessible = true
                    constructor.newInstance()
                },
                it,
                path
            )}
            .reversed()

        // Add all classes
        leaves += clazz.declaredClasses
            .filter { !it.kotlin.isCompanion }
            .map { Group(it, path) }
            .map { it.withChildren(getBeans(it.clazz, "$path ${it.name}")) }

        return leaves
    }

    /**
     * Generates a coffee bean from a method.
     * @param method The method to generate the coffee bean from.
     * @return The coffee bean.
     */
    @JvmStatic
    fun createBean(parent: Any, method: Method, path: String): Bean {
        val parameters = ParameterParser.getParameters(method)
            .map { it.resolve() }

        return Bean(
            NameResolver.resolveName(method),
            AnnotationResolver.hasAnnotation(method, Sync::class.java),
            parameters,
            parent,
            method,
            path
        )
    }

    /**
     * Checks if a class is a valid command.
     * @param clazz The class to check.
     * @return Whether the class is a valid command.
     * @param T The type of the class.
     */
    fun <T> isValid(clazz: Class<T>): Boolean {
        return AnnotationResolver.hasAnnotation(clazz, Command::class.java)
            && !clazz.isLocalClass
            && !clazz.isMemberClass
            && clazz.declaringClass == null
    }

    /**
     * Whether a method is capable of becoming a command / subcommand.
     */
    fun isValid(method: Method): Boolean {
        if (method.returnType != Void.TYPE) return false
        if (method.kotlinFunction != null) {
            val data = method.kotlinFunction!!
            if (data.isAbstract || data.isExternal || data.isInfix || data.isInline || data.isOperator) return false
        }

        if (method.kotlinFunction != null) {
            val data = method.kotlinFunction!!
            if (data.visibility != KVisibility.PUBLIC || data.isSuspend) return false
        } else {
            if (!Modifier.isPublic(method.modifiers) || Modifier.isStatic(method.modifiers)) return false
        }

        return !(method.isAnnotationPresent(ParserIgnore::class.java)
            || method.isAnnotationPresent(JvmStatic::class.java))
    }


}