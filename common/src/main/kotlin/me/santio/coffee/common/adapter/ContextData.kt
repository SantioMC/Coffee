package me.santio.coffee.common.adapter

import me.santio.coffee.common.models.tree.CommandTree

/**
 * Additional data for all parameter contexts.
 */
open class ContextData(
    open val tree: CommandTree<*>?
)