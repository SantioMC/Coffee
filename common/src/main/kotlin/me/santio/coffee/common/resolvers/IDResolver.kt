package me.santio.coffee.common.resolvers

import java.util.*

/**
A simple object made for creating unique ids
 */
object IDResolver {
    
    private fun createID() = UUID.randomUUID().toString().substring(0, 16)
    
    /**
     * Create a new randomly generated id, this takes the first 16 characters of a random uuid and prepends
     * "coffee-" to the beginning of it.
     * @return A randomly generated string prepended by "coffee-"
     */
    fun id() = "coffee-${createID()}"
    
}