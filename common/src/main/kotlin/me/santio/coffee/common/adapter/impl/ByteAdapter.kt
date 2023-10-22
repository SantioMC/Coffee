package me.santio.coffee.common.adapter.impl

import me.santio.coffee.common.adapter.ArgumentAdapter
import me.santio.coffee.common.adapter.ContextData

object ByteAdapter: ArgumentAdapter<Byte>() {
    override val type: Class<Byte> = Byte::class.java

    override fun adapt(arg: String, context: ContextData): Byte? {
        return arg.toByteOrNull()
    }
}