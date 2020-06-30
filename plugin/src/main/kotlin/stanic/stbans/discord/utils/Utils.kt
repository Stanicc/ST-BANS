package stanic.stbans.discord.utils

import net.dv8tion.jda.api.entities.*
import stanic.stbans.Main

fun TextChannel.reply(message: String) = sendMessage(message).complete()
fun TextChannel.reply(embed: MessageEmbed) = sendMessage(embed).complete()
fun TextChannel.reply(embed: MessageEmbed, success: Message.() -> Unit) = sendMessage(embed).queue {
    it.apply(success)
}

fun Member.reply(message: String) = user.openPrivateChannel().complete().sendMessage(message).complete()
fun Member.reply(embed: MessageEmbed) = user.openPrivateChannel().complete().sendMessage(embed).complete()

fun List<String>.toMessage(): String {
    var message = ""
    for (line in this) message = "$message$line\n"

    return message
}

fun String.get() = Main.settings.getString("Messages.$this")!!