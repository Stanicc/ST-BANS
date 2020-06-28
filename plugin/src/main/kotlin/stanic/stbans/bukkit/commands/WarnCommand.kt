package stanic.stbans.bukkit.commands

import org.bukkit.Bukkit
import stanic.stbans.Main
import stanic.stbans.discord.DiscordBot
import stanic.stbans.discord.sendToDiscord
import stanic.stbans.factory.model.PlayerInfo
import stanic.stbans.utils.Messages
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.message.send

fun Main.registerWarnCommand() = command("warn") { sender, args ->
    if (!sender.hasPermission("stbans.warncmd")) {
        sender.send(Messages().get("noPerm"))
        return@command
    }

    if (args.size < 2) {
        sender.send(Messages().get("usageWarn"))
        return@command
    }

    var reason = ""
    (1 until args.size).forEach { i ->
        reason = "$reason${args[i]} "
    }

    Bukkit.getPlayer(args[0])?.run {
        send(Messages().get("warnPlayer").replace("{reason}", reason).replace("{staff}", sender.name))
        sender.send(Messages().get("warnStaff").replace("{reason}", reason).replace("{nick}", this.name))

        if (!Main.instance.playerInfo.containsKey(args[0])) {
            val playerInfo = PlayerInfo(args[0], 0, ArrayList())
            Main.instance.playerInfo[args[0]] = playerInfo
        }
        Main.instance.playerInfo[args[0]]!!.warns += 1

        if (Main.settings.getBoolean("Config.warnLimit") && Main.instance.playerInfo[args[0]]!!.warns >= Main.settings.getInt(
                "Config.warnLimiteNumber"
            )
        ) {
            for (action in Main.settings.getStringList("Config.warnActions")) Bukkit.getConsoleSender().command(
                action.replace("{nick}", this.name).replace("{reason}", reason).replace("{staff}", sender.name)
            )
            Main.instance.playerInfo[args[0]]!!.warns = 0
        }

        if (Main.settings.getBoolean("Config.alertWarnBroadcast")) {
            Bukkit.getOnlinePlayers().forEach {
                it.send(
                    Messages().get("playerWarnedBroadcast").replace("{reason}", reason).replace(
                        "{nick}",
                        this.name
                    ).replace("{staff}", sender.name)
                )
            }
        }

        if (DiscordBot.enabled && Main.settings.getBoolean("Discord.channels.enableWarn")) {
            val message =
                "${Main.settings.getString("Discord.channels.warnMessage.title").replace(
                    "{reason}",
                    reason
                ).replace(
                    "{nick}",
                    this.name
                ).replace("@n", "\n").replace(
                    "{staff}",
                    sender.name
                )}[/title/][/body/]${Main.settings.getString("Discord.channels.warnMessage.body").replace(
                    "{reason}",
                    reason
                ).replace(
                    "{nick}",
                    this.name
                ).replace("{staff}", sender.name).replace("@n", "\n")}"

            message.sendToDiscord(Main.settings.getString("Discord.channels.warnChannel"))
        }
        return@command
    }

    sender.send(Messages().get("playerOffline"))
}