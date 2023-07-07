package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter

object IntegerAdapter: ArgumentAdapter<Int>() {
    override val type: Class<Int> = Int::class.java

    override fun adapt(arg: String): Int? {
        return arg.toIntOrNull()
    }
}