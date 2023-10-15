package me.santio.coffee.common.models.tree

import me.santio.coffee.common.models.Parent
import me.santio.coffee.common.resolvers.NameResolver

/**
 * Represents a group holding commands.
 */
data class Group(
    val clazz: Class<*>,
    override val path: String
): Leaf(clazz, path), Parent {
    override val aliases: List<String> = listOf(NameResolver.generateName(clazz))

    /**
     * Adds children to the group.
     * @param children The children to add.
     * @return The group object instance.
     */
    fun withChildren(vararg children: Leaf): Group {
        this.children.addAll(children)
        return this
    }

    /**
     * Adds children to the group.
     * @param children The list of children to add.
     * @return The group object instance.
     */
    fun withChildren(children: Collection<Leaf>): Group {
        this.children.addAll(children)
        return this
    }
}