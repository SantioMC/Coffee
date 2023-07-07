package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter

object StringAdapter: ArgumentAdapter<String>() {
    override val type: Class<String> = String::class.java
    override fun adapt(arg: String): String = arg
}