package stanic.stbans.discord

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import stanic.stbans.Main
import stanic.stbans.discord.DiscordBot.Companion.dc
import stanic.stbans.discord.commands.ban.*
import stanic.stbans.discord.commands.mute.*
import stanic.stbans.discord.commands.registerInfoCommand

class DiscordBot {

    companion object {
        var enabled = false
        lateinit var dc: ShardManager
    }

    fun start() {
        if (Main.settings.getBoolean("Discord.enable"))
            dc = DefaultShardManagerBuilder()
                .setToken(Main.settings.getString("Discord.token"))
                .setActivity(Activity.playing(Main.settings.getString("Discord.game")))
                .build().apply {
                    shards.first().run {
                        registerBanCommand()
                        registerBanIpCommand()
                        registerTempBanCommand()
                        registerTempBanIpCommand()
                        registerUnbanCommand()

                        registerMuteCommand()
                        registerMuteIpCommand()
                        registerTempMuteCommand()
                        registerTempMuteIpCommand()
                        registerUnmuteCommand()

                        registerInfoCommand()
                    }
                    enabled = true
                }
    }

}

fun String.sendToDiscord(channel: String) {
    if (!DiscordBot.enabled) return

    val embed = EmbedBuilder()
        .setTitle(this.split("[/title/]")[0])
        .setDescription(this.split("[/body/]")[1])

    dc.getTextChannelById(channel)?.sendMessage(embed.build())!!.complete()
}