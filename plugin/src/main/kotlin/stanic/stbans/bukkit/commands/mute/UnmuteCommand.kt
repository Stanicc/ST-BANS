package stanic.stbans.bukkit.commands.mute

import stanic.stbans.Main
import stanic.stbans.factory.PunishFactory
import stanic.stbans.utils.Messages
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.message.send

/**
 *
 * UnmuteCommand class
 *
 */

class UnmuteCommand {

    fun run(m: Main) = m.command("unmute") { sender, args ->
        if (!sender.hasPermission("stbans.revokecmd")) {
            sender.send(Messages().get("noPerm"))
            return@command
        }

        if (args.isEmpty()) {
            sender.send(Messages().get("usageUnmute"))
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