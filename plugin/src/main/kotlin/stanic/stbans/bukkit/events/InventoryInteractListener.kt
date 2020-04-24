package stanic.stbans.bukkit.events

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import stanic.stbans.Main
import stanic.stbans.factory.model.PlayerInfo
import stanic.stbans.utils.Menus
import stanic.stbans.utils.Messages
import stanic.stutils.bukkit.event.event
import stanic.stutils.bukkit.message.replaceColor
import stanic.stutils.bukkit.message.send

class InventoryInteractListener {

    companion object {
        val reports = HashMap<Int, String>()
        val playerInfo = HashMap<Int, PlayerInfo>()
    }

    fun onInteractInReports(m: Main) = m.event<InventoryClickEvent> { event ->
        if (event.inventory.title == Main.settings.getString("Menus.reports.name").replaceColor()) {
            val player = event.whoClicked as Player

            when (event.slot) {
                53 -> {
                    Menus.pag += 1
                    Menus.openReportsMenu(player)
                }
                45 -> {
                    Menus.pag -= 1
                    Menus.openReportsMenu(player)
                }
                else -> {
                    if (event.currentItem != null && event.currentItem.type != Material.AIR && playerInfo.containsKey(
                            event.slot
                        )
                    ) {
                        if (event.isLeftClick) {
                            playerInfo[event.slot]!!.reports.remove(reports[event.slot]!!)

                            if (playerInfo[event.slot]!!.reports.isEmpty()) {
                                val c = Main.instance.db
                                c.open()
                                c.statement!!.execute("DELETE FROM reports WHERE player='${playerInfo[event.slot]!!.player}'")
                                c.close()
                            }

                            player.closeInventory()
                        } else {
                            if (Bukkit.getPlayer(playerInfo[event.slot]!!.player) != null) {
                                player.teleport(Bukkit.getPlayer(playerInfo[event.slot]!!.player))
                                player.closeInventory()
                            } else player.send(Messages().get("playerOffline"))
                        }
                    }
                }
            }
            event.isCancelled = true
        }
    }

    fun onInteractInHistoric(m: Main) = m.event<InventoryClickEvent> { event ->
        if (event.inventory.title == Main.settings.getString("Menus.historic.name").replaceColor()) {
            val player = event.whoClicked as Player

            when (event.slot) {
                53 -> {
                    Menus.pag += 1
                    Menus.openHistoricMenu(player)
                }
                45 -> {
                    Menus.pag -= 1
                    Menus.openHistoricMenu(player)
                }
            }

            event.isCancelled = true
        }
    }

}