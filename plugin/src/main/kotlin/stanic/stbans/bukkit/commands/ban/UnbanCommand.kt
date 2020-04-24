package stanic.stbans.bukkit.commands.ban

import stanic.stbans.Main
import stanic.stbans.factory.PunishFactory
import stanic.stbans.utils.Messages
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.message.send

/**
 *
 * UnBanCommand class
 *
 */

class UnbanCommand {

    fun run(m: Main) = m.command("unban") { sender, args ->
        if (!sender.hasPermission("stbans.bancmd")) {
            sender.send(Messages().get("noPerm"))
            return@command
        }

        if (!sender.hasPermission("stbans.revokecmd")) {
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
            sender.send(Messages().get("numbersOnly"))
        }

    }

}