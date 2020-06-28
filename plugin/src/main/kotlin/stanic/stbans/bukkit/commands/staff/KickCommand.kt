package stanic.stbans.bukkit.commands.staff

import org.bukkit.Bukkit
import stanic.stbans.Main
import stanic.stbans.discord.DiscordBot
import stanic.stbans.discord.sendToDiscord
import stanic.stbans.utils.Messages
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.message.send

fun Main.registerKickCommand() = command("kick") { sender, args ->
    if (!sender.hasPermission("stbans.kickcmd")) {
        sender.send(Messages().get("noPerm"))
        return@command
    }

    if (args.size < 2) {
        sender.send(Messages().get("usageKick"))
        return@command
    }

    var reason = ""
    (1 until args.size).forEach { i ->
        reason = "$reason${args[i]} "
    }

    Bukkit.getPlayer(args[0])?.run {
        this.kickPlayer(
            Messages().get("kickMessage").replace("{reason}", reason).replace(
                "{nick}",
                this.name
            ).replace("{staff}", sender.name)
        )
        sender.send(Messages().get("playerKicked").replace("{reason}", reason).replace("{nick}", this.name))

        if (Main.settings.getBoolean("Config.alertKickBroadcast")) {
            Bukkit.getOnlinePlayers().forEach {
                it.send(
                    Messages().get("playerKickedBroadcast").replace("{reason}", reason).replace(
                        "{nick}",
                        this.name
                    ).replace("{staff}", sender.name)
                )
            }
        }

        if (DiscordBot.enabled && Main.settings.getBoolean("Discord.channels.enableKick")) {
            val message =
                "${Main.settings.getString("Discord.channels.kickMessage.title").replace("{reason}", reason).replace(
                    "{nick}",
                    this.name
                ).replace("@n", "\n").replace(
                    "{staff}",
                    sender.name
                )}[/title/][/body/]${Main.settings.getString("Discord.channels.kickMessage.body").replace(
                    "{reason}",
                    reason
                ).replace(
                    "{nick}",
                    this.name
                ).replace("@n", "\n").replace("{staff}", sender.name)}"

            message.sendToDiscord(Main.settings.getString("Discord.channels.kickChannel"))
        }
        return@command
    }

    sender.send(Messages().get("playerOffline"))
}