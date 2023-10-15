package me.santio.coffee.common.resolvers

import me.santio.coffee.common.models.tree.Bean
import me.santio.coffee.common.models.tree.CommandTree
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method

/**
 * This object is used to get the proper applying annotation for a type.
 */
@Suppress("MemberVisibilityCanBePrivate")
object AnnotationResolver {


    /**
     * Get the annotation from the type.
     * @param type The type to get the annotation from.
     * @param annotation The annotation to get.
     * @param scope The scope to search in.
     */
    @JvmStatic
    @JvmOverloads
    fun <T: Annotation> getAnnotation(type: AnnotatedElement, annotation: Class<T>, scope: Scope = Scope.SELF): T? {
        type.getAnnotation(annotation)?.let { return it }
        if (scope == Scope.SELF) return null

        val parent = when (type) {
            is Class<*> -> type.superclass
            is Method -> type.declaringClass
            else -> return null
        } ?: return null

        return getAnnotation(
            parent,
            annotation,
            if (scope == Scope.PARENT) Scope.SELF else Scope.ALL
        )
    }

    /**
     * Get the annotation from a command bean.
     * @param bean The bean to get the annotation from.
     * @param annotation The annotation to get.
     * @param scope The scope to search in.
     */
    @JvmStatic
    @JvmOverloads
    fun <T: Annotation> getAnnotation(bean: Bean, annotation: Class<T>, scope: Scope = Scope.SELF): T? {
        return getAnnotation(bean.method, annotation, scope)
    }

    /**
     * Get the annotation from the type.
     * @param bean The bean to get the annotation from.
     * @param annotation The annotation to get.
     * @param scope The scope to search in.
     */
    @JvmStatic
    @JvmOverloads
    fun <T: Annotation> getAnnotation(bean: Bean, annotation: T, scope: Scope = Scope.SELF): T? {
        return getAnnotation(bean.method, annotation::class.java, scope)
    }

    /**
     * Get the annotation from a command bean.
     * @param tree The tree to get the annotation from.
     * @param annotation The annotation to get.
     * @param scope The scope to search in.
     */
    @JvmStatic
    @JvmOverloads
    fun <T: Annotation> getAnnotation(tree: CommandTree<*>, annotation: Class<T>, scope: Scope = Scope.SELF): T? {
        return getAnnotation(tree.instance!!::class.java, annotation, scope)
    }

    /**
     * Get the annotation from the type.
     * @param tree The tree to get the annotation from.
     * @param annotation The annotation to get.
     * @param scope The scope to search in.
     */
    @JvmStatic
    @JvmOverloads
    fun <T: Annotation> getAnnotation(tree: CommandTree<*>, annotation: T, scope: Scope = Scope.SELF): T? {
        return getAnnotation(tree.instance!!::class.java, annotation::class.java, scope)
    }

    /**
     * Get the annotation from the type.
     * @param type The type to get the annotation from.
     * @param annotation The annotation to get.
     * @param scope The scope to search in.
     */
    @JvmStatic
    @JvmOverloads
    fun <T: Annotation> getAnnotation(type: AnnotatedElement, annotation: T, scope: Scope = Scope.SELF): T? {
        return getAnnotation(type, annotation::class.java, scope)
    }

    /**
     * Checks if the type has the annotation.
     * @param type The type to check.
     * @param annotation The annotation to check for.
     * @param scope The scope to search in.
     */
    @JvmStatic
    @JvmOverloads
    fun <T: Annotation> hasAnnotation(type: AnnotatedElement, annotation: Class<T>, scope: Scope = Scope.SELF): Boolean {
        return getAnnotation(type, annotation, scope) != null
    }

}