package me.santio.coffee.bukkit.utils

import me.santio.coffee.bukkit.annotations.Permission
import java.lang.reflect.Method

internal object AnnotationUtils {

    @Suppress("SENSELESS_COMPARISON")
    fun getPermission(method: Method): String? {
        val permissionSections = mutableListOf<String>()
        var permission = method.getAnnotation(Permission::class.java)

        // Handle specific permissions for the method
        if (permission != null) {
            if (permission.value == "none") return null
            else if (permission.value.contains(".") || permission.value == "op") return permission.value
            else permissionSections.add(permission.value)
        }

        // Calculate the permission for the method
        fun build(): String? {
            if (permissionSections.isEmpty()) return null
            if (permissionSections.contains("op")) return "op"
            return permissionSections.reversed().joinToString(".")
        }

        // Handle permissions for the class
        var clazz = method.declaringClass
        while (clazz != null) {
            permission = clazz.getAnnotation(Permission::class.java)

            if (permission != null) {
                if (permission.value.contains(".")) {
                    permissionSections.addAll(permission.value.split("."))
                    return build()
                }

                if (permission.value == "none") return build()
                permissionSections.add(permission.value)
            }

            clazz = clazz.declaringClass
        }

        return build()
    }

}