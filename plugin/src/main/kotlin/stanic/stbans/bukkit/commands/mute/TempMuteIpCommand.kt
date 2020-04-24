package stanic.stbans.bukkit.commands.mute

import org.bukkit.Bukkit
import stanic.stbans.Main
import stanic.stbans.controller.PunishController
import stanic.stbans.factory.model.Punishment
import stanic.stbans.utils.Messages
import stanic.stbans.utils.TimeUtils
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.command.isPlayer
import stanic.stutils.bukkit.message.send
import java.util.concurrent.TimeUnit

class TempMuteIpCommand {

    fun run(m: Main) = m.command("tempmute") { sender, args ->
        if (!sender.hasPermission("stbans.mutecmd")) {
            sender.send(Messages().get("noPerm"))
            return@command
        }

        if (args.size < 4) {
            sender.send(Messages().get("usageTempmute"))
            return@command
        }

        var reason = ""
        (3 until args.size).forEach { i ->
            reason = "$reason${args[i]} "
        }

        val id = Main.instance.punishment.size + 1

        val punish = Punishment(
            args[0],
            sender.name,
            "TempMute",
            reason,
            TimeUtils.getDate(),
            TimeUtils.getHour(),
            args[1].toLong(),
            id,
            Bukkit.getOfflinePlayer(args[0]).player.address.hostString,
            false
        )
        Main.instance.punishment[id] = punish

        PunishController().applyPunishment(
            args[0], id, when (args[2]) {
                "s" -> TimeUnit.SECONDS
                "m" -> TimeUnit.MINUTES
                "h" -> TimeUnit.HOURS
                "d" -> TimeUnit.DAYS
                else -> TimeUnit.DAYS
            }
        )

        if (!sender.isPlayer()) {
            sender.send("§cYou muted the player ${args[0]} address")
        }
    }

}