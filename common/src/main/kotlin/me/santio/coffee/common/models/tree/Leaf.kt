package me.santio.coffee.common.models.tree

import java.lang.reflect.AnnotatedElement

/**
 * Represents an extension of a command tree.
 */
abstract class Leaf(
    val type: AnnotatedElement,
    open val path: String,
    open val aliases: List<String> = listOf(),
    open val children: MutableList<Leaf> = mutableListOf()
) {
    /**
     * Gets the primary name of the leaf, which is just the first supplied
     * alias.
     */
    val name: String
        get() = aliases.first()

    /**
     * The path along with the leaf name attached (the path alone is purely the parent path)
     */
    val fullPath: String
        get() = "$path $name"
}