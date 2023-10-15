package me.santio.coffee.common.models

import me.santio.coffee.common.models.tree.Bean
import me.santio.coffee.common.models.tree.CommandTree
import me.santio.coffee.common.models.tree.Group
import me.santio.coffee.common.models.tree.Leaf

interface Parent {
    val name: String
    val children: MutableList<Leaf>

    /**
     * Find all possible executable beans.
     * @return A map of all possible executable beans.
     */
    fun all(): Map<String, Bean> {
        val beans = mutableMapOf<String, Bean>()

        children.forEach {
            when (it) {
                is Bean -> beans["$name ${it.name}"] = it
                is Group -> beans.putAll(it.all().mapKeys { (key, _) -> "$name $key" })
            }
        }

        return beans
    }

    /**
     * Find the closest matching bean based on the given query.
     * @param query The query to match against.
     * @return The closest matching bean.
     */
    fun find(query: String): Bean? {
        return all().values.toList()
            .filter { query.startsWith(it.fullPath) }
            .maxByOrNull { it.fullPath.split(" ").size }
    }

    /**
     * Builds a representation of the tree.
     * @return A string builder containing the tree.
     */
    @Suppress("NestedLambdaShadowedImplicitParameter")
    fun buildTree(): StringBuilder {
        val builder = StringBuilder()
        if (this is CommandTree<*>) builder.appendLine(name)

        children.forEach {
            when (it) {
                is Bean -> builder.appendLine("  └── ${it.name} - ${it.fullPath}")
                is Group -> {
                    builder.appendLine("  └── ${it.name} - ${it.fullPath}")
                    builder.append(it.buildTree().lines().joinToString("\n") { "    $it" })
                }
            }
        }

        return builder
    }
}