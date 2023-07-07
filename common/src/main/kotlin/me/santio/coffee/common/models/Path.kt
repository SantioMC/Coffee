package me.santio.coffee.common.models

import me.santio.coffee.common.parser.ClassParser
import me.santio.coffee.common.parser.MethodParser
import java.lang.reflect.Method

data class Path(
    val sections: MutableList<Section>,
) {

    companion object {
        @JvmStatic
        @get:JvmName("EMPTY")
        val EMPTY = Path(mutableListOf())

        @JvmStatic
        @JvmName("from")
        fun from(path: String): Path {
            val sections = path.split(" ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { Section(it, listOf(it)) }

            return Path(sections.toMutableList())
        }

        @JvmStatic
        @JvmName("from")
        fun from(clazz: Class<*>): Path = ClassParser.getPath(clazz)

        @JvmStatic
        @JvmName("from")
        fun from(method: Method): Path = MethodParser.getPath(method)

        @JvmStatic
        @JvmName("from")
        fun from(command: SubCommand): Path = from(command.method)
    }

    override fun hashCode(): Int = super.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is Path) return false
        if (sections.size != other.sections.size) return false

        for ((index, section) in sections.withIndex()) {
            for (alias in section.aliases) {
                val other = other.sections[index]
                if (!other.aliases.contains(alias)) return false
            }
        }

        return true
    }

    override fun toString(): String = sections.joinToString(" ") { it.name }

    data class Section(
        val name: String,
        val aliases: List<String>
    )

}
