package me.santio.coffee.common.models.tree

import me.santio.coffee.common.models.Parent

/**
 * Represents a command tree.
 * @param name The name of the command tree.
 * @param instance The instance of the class that the tree was generated from.
 * @param _children The children of the command tree.
 * @see Bean
 */
data class CommandTree<T>(
    val aliases: List<String>,
    val instance: T,
    override val children: MutableList<Leaf> = mutableListOf()
): Parent {
    override val name: String = aliases.firstOrNull() ?: throw IllegalArgumentException("Command tree has no name")
}