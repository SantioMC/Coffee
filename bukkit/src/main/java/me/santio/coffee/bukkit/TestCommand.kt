package me.santio.coffee.bukkit

import me.santio.coffee.bukkit.annotations.Permission
import me.santio.coffee.bukkit.utils.AnnotationUtils
import me.santio.coffee.common.annotations.Command
import me.santio.coffee.common.annotations.Sync
import java.io.PrintStream

object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val permission = AnnotationUtils.getPermission(TestCommand.Users.Manage.javaClass.getMethod("print", PrintStream::class.java))
        println(permission)
    }
}

@Command
@Permission("test")
object TestCommand {
    object Users {
        object Manage {

            fun print(out: PrintStream) {
                out.println("Hello, world!")
            }

            @Sync
            fun main(out: PrintStream) {
                println("ok")
            }

        }
    }
}