package stanic.stbans.discord.utils

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import stanic.stbans.Main

fun JDA.command(
    name: String,
    block: GuildMessageReceivedEvent.() -> Unit
) {
    addEventListener(object : ListenerAdapter() {
        override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
            if (event.message.contentRaw.toLowerCase().startsWith("${Main.settings.getString("Discord.prefix")}${name.toLowerCase()}")) event.apply(block)
        }
    })
}