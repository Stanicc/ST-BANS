package stanic.stbans.bukkit.commands.staff

import org.bukkit.entity.Player
import stanic.stbans.Main
import stanic.stbans.factory.model.PlayerInfo
import stanic.stbans.utils.Menus
import stanic.stbans.utils.Messages
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.message.send

/**
 *
 * ReportsCommand class
 *
 */

class ReportsCommand {

    fun run(m: Main) = m.command("reports") { sender, args ->
        if (sender !is Player) sender.send("§cThis command is allowed in-game only")
        else {
            if (!sender.hasPermission("stbans.reportadm")) {
                sender.send(Messages().get("noPerm"))
                return@command
            }

            if (args.isEmpty()) {
                if (PlayerInfo.all.isNotEmpty()) {
                    Menus.pag = 1
                    Menus.openReportsMenu(sender)
                } else sender.send(Messages().get("noReports"))
            } else if (args[0] == "clear") {
                PlayerInfo.all.forEach { it.reports.clear() }
                sender.send(Messages().get("reportsCleared"))
            }
        }
    }

}