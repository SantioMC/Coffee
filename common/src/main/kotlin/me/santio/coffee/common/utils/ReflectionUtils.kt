package me.santio.coffee.common.utils

/**
 * Some internal reflection utilities.
 */
internal object ReflectionUtils {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> convertListToArray(list: List<T>, type: Class<*>): Array<T?> {
        val array = java.lang.reflect.Array.newInstance(type, list.size) as Array<T?>
        for (i in list.indices) array[i] = list[i]
        return array
    }

}