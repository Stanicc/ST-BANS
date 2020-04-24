package stanic.stbans.bukkit.commands

import org.bukkit.Bukkit
import stanic.stbans.Main
import stanic.stbans.discord.DiscordBot
import stanic.stbans.discord.sendToDiscord
import stanic.stbans.factory.model.PlayerInfo
import stanic.stbans.utils.Messages
import stanic.stbans.utils.TimeUtils
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.message.send

/**
 *
 * ReportCommand class
 *
 */

class ReportCommand {

    fun run(m: Main) = m.command("report") { sender, args ->
        if (!sender.hasPermission("stbans.reportcmd")) {
            sender.send(Messages().get("noPerm"))
            return@command
        }

        if (args.size < 2) {
            sender.send(Messages().get("usageReport"))
            return@command
        }

        var reason = ""
        (1 until args.size).forEach { i ->
            reason = "$reason${args[i]} "
        }

        Bukkit.getOnlinePlayers().filter { it.hasPermission("stbans.reportadm") }.forEach {
            it.send(Messages().get("playerReportedStaff").replace("{nick}", args[0]).replace("{reason}", reason).replace("{report}", sender.name))
        }

        sender.send(Messages().get("playerReported").replace("{nick}", args[0]).replace("{reason}", reason).replace("{report}", sender.name))

        if (!Main.instance.playerInfo.containsKey(args[0])) {
            val playerInfo = PlayerInfo(args[0], 0, ArrayList())
            Main.instance.playerInfo[args[0]] = playerInfo
        }
        Main.instance.playerInfo[args[0]]!!.reports.add("${args[0]}[+]${sender.name}[+]$reason[+]${TimeUtils.getDate()}[+]${TimeUtils.getHour()}")

        if (DiscordBot.enabled && Main.settings.getBoolean("Discord.channels.enableReport")) {
            val message =
                "${Main.settings.getString("Discord.channels.reportMessage.title").replace(
                    "@n",
                    "\n"
                ).replace("{reason}", reason).replace("{report}", sender.name).replace(
                    "{nick}",
                    args[0]
                )}[/title/][/body/]${Main.settings.getString("Discord.channels.reportMessage.body").replace(
                    "{reason}",
                    reason
                ).replace("@n", "\n").replace("{report}", sender.name).replace(
                    "{nick}",
                    args[0]
                ).replace("{date}", TimeUtils.getDate()).replace("{hour}", TimeUtils.getHour())}"

            message.sendToDiscord(Main.settings.getString("Discord.channels.reportsChannel"))
        }
    }

}