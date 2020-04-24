package stanic.stbans.bukkit.commands.staff

import org.bukkit.entity.Player
import stanic.stbans.Main
import stanic.stbans.factory.PunishFactory
import stanic.stbans.utils.Menus
import stanic.stbans.utils.Messages
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.message.send

/**
 *
 * VerifyCommand class
 *
 */

class VerifyCommand {

    fun run(m: Main) = m.command("verify") { sender, args ->
        if (sender !is Player) sender.send("§cThis command is allowed in-game only")
        else {
            if (!sender.hasPermission("stbans.verifycmd")) {
                sender.send(Messages().get("noPerm"))
                return@command
            }

            if (!sender.hasPermission("stbans.verifycmd")) {
                sender.send(Messages().get("noPerm"))
                return@command
            }

            if (args.isEmpty()) {
                sender.send(Messages().get("usageVerify"))
                return@command
            }

            if (args.size < 2) {

                val historic = PunishFactory().getHistoric(args[0]).sortedByDescending { it.id }
                if (historic.isNotEmpty()) {
                    Menus.pag = 1
                    Menus.punishments = historic
                    Menus.openHistoricMenu(sender)
                } else sender.send(Messages().get("noHistoric"))
            } else {
                try {
                    val historic = PunishFactory().getHistoric(args[0], args[1].toInt()).sortedByDescending { it.id }
                    if (historic.isNotEmpty()) {
                        Menus.pag = 1
                        Menus.punishments = historic
                        Menus.openHistoricMenu(sender)
                    }
                    else sender.send(Messages().get("noHistoric"))
                } catch (e: NumberFormatException) {
                    sender.send(Messages().get("numbersOnly"))
                }
            }
        }
    }

}