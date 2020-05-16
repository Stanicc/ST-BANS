package stanic.stbans.utils

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import stanic.stbans.Main
import stanic.stbans.bukkit.events.InventoryInteractListener
import stanic.stbans.factory.model.PlayerInfo
import stanic.stbans.factory.model.Punishment
import stanic.stutils.bukkit.message.replaceColor
import stanic.stutils.bukkit.utils.ItemBuilder
import stanic.stutils.bukkit.utils.SkullUtils

class Menus {

    companion object {
        var pag = 1
        lateinit var punishments: List<Punishment>

        fun openReportsMenu(sender: Player) {
            val inv = Bukkit.createInventory(null, 54, Main.settings.getString("Menus.reports.name").replaceColor())

            val reports = ArrayList<String>()
            val playerInfo = ArrayList<PlayerInfo>()

            for (playerI in PlayerInfo.all) playerI.reports.forEach {
                reports.add(it)
                playerInfo.add(playerI)
            }

            var slot = 11
            val size = reports.size

            for (i in 0..15) {
                if (i + (15 * (pag - 1)) < size) {
                    val lore = ArrayList<String>()
                    for (line in Main.settings.getStringList("Menus.reports.itens.reportItem.lore")) lore.add(
                        line
                            .replaceColor()
                            .replace("{nick}", reports[i + (15 * (pag - 1))].split("[+]")[0])
                            .replace("{report}", reports[i + (15 * (pag - 1))].split("[+]")[1])
                            .replace("{reason}", reports[i + (15 * (pag - 1))].split("[+]")[2])
                            .replace("{date}", reports[i + (15 * (pag - 1))].split("[+]")[3])
                            .replace("{hour}", reports[i + (15 * (pag - 1))].split("[+]")[4])
                    )
                    inv.setItem(
                        slot,
                        if (!Main.settings.getString("Menus.reports.itens.reportItem.skullOwner").startsWith("http")) ItemBuilder(
                            ItemStack(Main.settings.getInt("Menus.reports.itens.reportItem.id"))
                        ).setDurability(
                            Main.settings.getInt(
                                "Menus.reports.itens.reportItem.data"
                            )
                        ).setSkullOwner(Main.settings.getString("Menus.reports.itens.reportItem.skullOwner").replace("{nick}", reports[i + (15 * (pag - 1))].split("[+]")[0])).setName(
                            Main.settings.getString("Menus.reports.itens.reportItem.name")
                                .replaceColor()
                                .replace("{nick}", reports[i + (15 * (pag - 1))].split("[+]")[0])
                                .replace("{report}", reports[i + (15 * (pag - 1))].split("[+]")[1])
                                .replace("{reason}", reports[i + (15 * (pag - 1))].split("[+]")[2])
                                .replace("{date}", reports[i + (15 * (pag - 1))].split("[+]")[3])
                                .replace("{hour}", reports[i + (15 * (pag - 1))].split("[+]")[4])
                        )
                            .setLore(lore).build()
                        else ItemBuilder(
                            SkullUtils().getSkull(Main.settings.getString("Menus.reports.itens.reportItem.skullOwner").replace("{nick}", reports[i + (15 * (pag - 1))].split("[+]")[0]))
                        ).setName(
                            Main.settings.getString("Menus.reports.itens.reportItem.name")
                                .replaceColor()
                                .replace("{nick}", reports[i + (15 * (pag - 1))].split("[+]")[0])
                                .replace("{report}", reports[i + (15 * (pag - 1))].split("[+]")[1])
                                .replace("{reason}", reports[i + (15 * (pag - 1))].split("[+]")[2])
                                .replace("{date}", reports[i + (15 * (pag - 1))].split("[+]")[3])
                                .replace("{hour}", reports[i + (15 * (pag - 1))].split("[+]")[4])
                        )
                            .setLore(lore).build()
                    )
                    InventoryInteractListener.reports[slot] = reports[i + (15 * (pag - 1))]
                    InventoryInteractListener.playerInfo[slot] = playerInfo[i + (15 * (pag - 1))]
                    if (++slot == 16) slot = 20
                    if (slot == 25) slot = 29
                    if (slot == 34) break
                }
            }

            if (slot >= 33) inv.setItem(
                53,
                ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner("MHF_ArrowRight").setName("§a--->").build()
            )
            if (pag >= 2) inv.setItem(
                45,
                ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner("MHF_ArrowLeft").setName("§c<---").build()
            )

            sender.openInventory(inv)
        }

        fun openHistoricMenu(sender: Player) {
            val inv = Bukkit.createInventory(
                null,
                54,
                Main.settings.getString("Menus.historic.name").replaceColor()
            )

            var slot = 11
            val size = punishments.size

            for (i in 0..15) {
                if (i + (15 * (pag - 1)) < size) {
                    val lore = ArrayList<String>()
                    for (line in Main.settings.getStringList("Menus.historic.itens.historicItem.lore")) lore.add(
                        line
                            .replaceColor()
                            .replaceInfo(punishments[i + (15 * (pag - 1))])
                    )
                    inv.setItem(
                        slot,
                        if (!Main.settings.getString("Menus.historic.itens.historicItem.skullOwner").startsWith("http")) ItemBuilder(
                            ItemStack(Main.settings.getInt("Menus.historic.itens.historicItem.id"))
                        ).setDurability(
                            Main.settings.getInt(
                                "Menus.historic.itens.historicItem.data"
                            )
                        ).setSkullOwner(Main.settings.getString("Menus.historic.itens.historicItem.skullOwner").replaceInfo(
                            punishments[i + (15 * (pag - 1))]
                        )).setName(
                            Main.settings.getString("Menus.historic.itens.historicItem.name").replaceColor().replaceInfo(
                                punishments[i + (15 * (pag - 1))]
                            )
                        ).setLore(lore).build()
                        else ItemBuilder(SkullUtils().getSkull(Main.settings.getString("Menus.historic.itens.historicItem.skullOwner").replaceInfo(
                            punishments[i + (15 * (pag - 1))]
                        ))).setName(
                            Main.settings.getString("Menus.historic.itens.historicItem.name").replaceColor().replaceInfo(
                                punishments[i + (15 * (pag - 1))]
                            )
                        ).setLore(lore).build()
                    )
                    if (++slot == 16) slot = 20
                    if (slot == 25) slot = 29
                    if (slot == 34) break
                }
            }

            if (slot >= 33) inv.setItem(
                53,
                ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner("MHF_ArrowRight").setName("§aPróxima página").build()
            )
            if (pag >= 2) inv.setItem(
                45,
                ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner("MHF_ArrowLeft").setName("§cPágina anterior").build()
            )

            sender.openInventory(inv)
        }
    }

}