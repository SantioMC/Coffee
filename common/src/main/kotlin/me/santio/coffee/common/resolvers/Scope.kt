package me.santio.coffee.common.resolvers


/**
 * The scope to search an annotation in.
 */
enum class Scope {
    /**
     * Search the type itself and nothing more.
     * searches: Type
     */
    SELF,

    /**
     * Search both the type and it's parent class.
     * searches: Declaring Class -> Type
     */
    PARENT,

    /**
     * Search the entire hierarchy of the type.
     * searches: Super Class -> Super Class -> Declaring Class -> Type
     */
    ALL
}