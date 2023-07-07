@Command
object PingCommand {
    fun main(sender: CommandSender) {
        sender.sendMessage("Pong!")
    }
}