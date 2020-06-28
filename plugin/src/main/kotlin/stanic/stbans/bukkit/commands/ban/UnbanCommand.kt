package stanic.stbans.bukkit.commands.ban

import org.bukkit.entity.Player
import stanic.stbans.Main
import stanic.stbans.factory.PunishFactory
import stanic.stbans.utils.Messages
import stanic.stbans.utils.openRevokeMenu
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.message.send

fun Main.registerUnbanCommand() = command("unban") { sender, args ->
    if (!sender.hasPermission("stbans.unbancmd")) {
        sender.send(Messages().get("noPerm"))
        return@command
    }

    if (args.isEmpty()) {
        sender.send(Messages().get("usageUnban"))
        return@command
    }

    try {
        if (PunishFactory().getPunishByID(Integer.parseInt(args[0])) != null) {
            PunishFactory().removePunishment(Integer.parseInt(args[0])).apply {
                sender.send(Messages().get("punishRepealed").replace("{id}", args[0]))
            }
        }
    } catch (e: NumberFormatException) {
        if (sender !is Player) sender.send("§cThis command is allowed in-game only")
        else {
            val punishments = PunishFactory().getHistoric(args[0], 1).filter { it.type.contains("Ban") }.sortedByDescending { it.id }
            if (punishments.isNotEmpty()) sender.openRevokeMenu(ArrayList(punishments))
            else sender.send(Messages().get("noPunishments"))
        }
    }
}