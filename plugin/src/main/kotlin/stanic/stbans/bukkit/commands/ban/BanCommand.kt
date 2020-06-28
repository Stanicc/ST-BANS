package stanic.stbans.bukkit.commands.ban

import org.bukkit.entity.Player
import stanic.stbans.Main
import stanic.stbans.controller.PunishController
import stanic.stbans.factory.model.Punishment
import stanic.stbans.utils.Messages
import stanic.stbans.utils.TimeUtils
import stanic.stbans.utils.replaceInfo
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.message.send

fun Main.registerBanCommand() = command("ban") { sender, args ->
    if (!sender.hasPermission("stbans.bancmd")) {
        sender.send(Messages().get("noPerm"))
        return@command
    }

    if (args.size < 2) {
        sender.send(Messages().get("usageBan"))
        return@command
    }

    var reason = ""
    (1 until args.size).forEach { i ->
        reason = "$reason${args[i]} "
    }

    val id = Main.instance.punishment.size + 1

    val punish = Punishment(
        args[0],
        sender.name,
        "Ban",
        reason,
        TimeUtils.getDate(),
        TimeUtils.getHour(),
        null,
        id,
        isActive = false
    )
    Main.instance.punishment[id] = punish
    Main.instance.playerInfo.remove(args[0])

    PunishController().applyPunishment(args[0], id)

    if (sender !is Player) {
        sender.send(Messages().get("punishmentAppliedStaff").replaceInfo(punish))
    }
}