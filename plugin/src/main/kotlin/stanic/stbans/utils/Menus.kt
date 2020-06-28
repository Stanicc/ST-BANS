package stanic.stbans.utils

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import stanic.stbans.Main
import stanic.stbans.factory.PunishFactory
import stanic.stbans.factory.model.PlayerInfo
import stanic.stbans.factory.model.Punishment
import stanic.stutils.bukkit.collections.ObservableList
import stanic.stutils.bukkit.command.command
import stanic.stutils.bukkit.dsl.menu.menu
import stanic.stutils.bukkit.dsl.menu.pagination.pagination
import stanic.stutils.bukkit.dsl.menu.pagination.slot
import stanic.stutils.bukkit.dsl.menu.slot
import stanic.stutils.bukkit.menu.slot.MenuPlayerInventorySlot
import stanic.stutils.bukkit.message.replace
import stanic.stutils.bukkit.message.replaceColor
import stanic.stutils.bukkit.message.send
import stanic.stutils.bukkit.utils.ItemBuilder
import stanic.stutils.bukkit.utils.SkullUtils

fun Player.openReportsMenu() {
    val reports = ArrayList<String>()
    for (playerInfo in PlayerInfo.all) playerInfo.reports.forEach {
        reports.add("${it}[+]${playerInfo.player}")
    }

    menu(Main.settings.getString("Menus.reports.name").replaceColor(), 6, Main.instance) {
        val nextItemSlot = slot(Main.settings.getInt("Menus.reports.items.nextPageItem.slot"), buildItem("Menus.reports.items.nextPageItem"))
        val previousItemSlot = slot(Main.settings.getInt("Menus.reports.items.previousPageItem.slot"), buildItem("Menus.reports.items.previousPageItem"))

        val pagination = pagination(
            ObservableList(reports.toMutableList()),
            nextItemSlot, previousItemSlot,
            startLine = Main.settings.getInt("Menus.reports.startLine"),
            endLine = Main.settings.getInt("Menus.reports.endLine"),
            startSlot = Main.settings.getInt("Menus.reports.startSlot"),
            endSlot = Main.settings.getInt("Menus.reports.endSlot")
        ) {
            slot {
                onRender {
                    if (it != null) {
                        val item = buildItem("Menus.reports.items.reportsItem")!!
                        val meta = item.itemMeta

                        val name = meta.displayName.replace("{nick}", it.split("[+]")[0])
                            .replace("{report}", it.split("[+]")[1])
                            .replace("{reason}", it.split("[+]")[2])
                            .replace("{date}", it.split("[+]")[3])
                            .replace("{hour}", it.split("[+]")[4])
                        val lore = meta.lore.replace("{nick}" to it.split("[+]")[0])
                            .replace("{report}" to it.split("[+]")[1])
                            .replace("{reason}" to it.split("[+]")[2])
                            .replace("{date}" to it.split("[+]")[3])
                            .replace("{hour}" to it.split("[+]")[4])

                        meta.displayName = name
                        meta.lore = lore
                        item.itemMeta = meta

                        showingItem = item
                    }
                }

                onClick {
                    if (it != null) {
                        Main.instance.playerInfo[it.split("[+]")[5]]!!.reports.remove("${it.split("[+]")[0]}[+]${it.split("[+]")[1]}[+]${it.split("[+]")[2]}[+]${it.split("[+]")[3]}[+]${it.split("[+]")[4]}")

                        if (Main.instance.playerInfo[it.split("[+]")[5]]!!.reports.isEmpty()) {
                            val c = Main.instance.db
                            c.open()
                            c.statement!!.execute("DELETE FROM reports WHERE player='${it.split("[+]")[5]}'")
                            c.close()
                        }

                        player.closeInventory()

                        if (Main.settings.get("Menus.reports.commands") != null)
                            for (cmd in Main.settings.getStringList("Menus.reports.commands")) player.command(cmd.replace("{player}", it.split("[+]")[5]))
                    }
                }
            }

            onPageChange {
                updateSlot(previousPageSlot, player)
                updateSlot(nextPageSlot, player)
            }
        }

        fun MenuPlayerInventorySlot.showItemWhenHasPage(type: String, hasPageLogic: () -> Boolean) {
            showingItem = if (hasPageLogic()) when (type) {
                "next" -> nextItemSlot.item
                "previous" -> previousItemSlot.item
                else -> null
            }
            else null
        }

        nextItemSlot.onRender { showItemWhenHasPage("next") { pagination.hasNextPage(player) } }
        nextItemSlot.onUpdate { showItemWhenHasPage("next") { pagination.hasNextPage(player) } }
        previousItemSlot.onRender { showItemWhenHasPage("previous") { pagination.hasPreviousPage(player) } }
        previousItemSlot.onUpdate { showItemWhenHasPage("previous") { pagination.hasPreviousPage(player) } }
    }.openToPlayer(this)
}

fun Player.openHistoricMenu(punishments: List<Punishment>) {
    menu(Main.settings.getString("Menus.historic.name").replaceColor(), 6, Main.instance) {
        val nextItemSlot = slot(Main.settings.getInt("Menus.historic.items.nextPageItem.slot"), buildItem("Menus.reports.items.nextPageItem"))
        val previousItemSlot = slot(Main.settings.getInt("Menus.historic.items.previousPageItem.slot"), buildItem("Menus.reports.items.previousPageItem"))

        val pagination = pagination(
            ObservableList(punishments.toMutableList()),
            nextItemSlot, previousItemSlot,
            startLine = Main.settings.getInt("Menus.historic.startLine"),
            endLine = Main.settings.getInt("Menus.historic.endLine"),
            startSlot = Main.settings.getInt("Menus.historic.startSlot"),
            endSlot = Main.settings.getInt("Menus.historic.endSlot")
        ) {
            slot {
                onRender {
                    if (it != null) {
                        val item = buildItem("Menus.historic.items.historicItem", it.nick)!!
                        val meta = item.itemMeta

                        val name = meta.displayName.replaceInfo(it)
                        val lore = ArrayList<String>()
                        for (line in meta.lore) lore.add(line.replaceInfo(it))

                        meta.displayName = name
                        meta.lore = lore
                        item.itemMeta = meta

                        showingItem = item
                    }
                }
            }

            onPageChange {
                updateSlot(previousPageSlot, player)
                updateSlot(nextPageSlot, player)
            }
        }

        fun MenuPlayerInventorySlot.showItemWhenHasPage(type: String, hasPageLogic: () -> Boolean) {
            showingItem = if (hasPageLogic()) when (type) {
                "next" -> nextItemSlot.item
                "previous" -> previousItemSlot.item
                else -> null
            }
            else null
        }

        nextItemSlot.onRender { showItemWhenHasPage("next") { pagination.hasNextPage(player) } }
        nextItemSlot.onUpdate { showItemWhenHasPage("next") { pagination.hasNextPage(player) } }
        previousItemSlot.onRender { showItemWhenHasPage("previous") { pagination.hasPreviousPage(player) } }
        previousItemSlot.onUpdate { showItemWhenHasPage("previous") { pagination.hasPreviousPage(player) } }
    }.openToPlayer(this)
}

fun Player.openRevokeMenu(punishments: ArrayList<Punishment>) {
    menu(Main.settings.getString("Menus.revoke.name").replaceColor(), 6, Main.instance) {
        val nextItemSlot = slot(Main.settings.getInt("Menus.revoke.items.nextPageItem.slot"), buildItem("Menus.reports.items.nextPageItem"))
        val previousItemSlot = slot(Main.settings.getInt("Menus.revoke.items.previousPageItem.slot"), buildItem("Menus.reports.items.previousPageItem"))

        val pagination = pagination(
            ObservableList(punishments.toMutableList()),
            nextItemSlot, previousItemSlot,
            startLine = Main.settings.getInt("Menus.revoke.startLine"),
            endLine = Main.settings.getInt("Menus.revoke.endLine"),
            startSlot = Main.settings.getInt("Menus.revoke.startSlot"),
            endSlot = Main.settings.getInt("Menus.revoke.endSlot")
        ) {
            slot {
                onRender {
                    if (it != null) {
                        val item = buildItem("Menus.revoke.items.revokeItem", it.nick)!!
                        val meta = item.itemMeta

                        val name = meta.displayName.replaceInfo(it)
                        val lore = ArrayList<String>()
                        for (line in meta.lore) lore.add(line.replaceInfo(it))

                        meta.displayName = name
                        meta.lore = lore
                        item.itemMeta = meta

                        showingItem = item
                    }
                }

                onClick {
                    if (it != null) {
                        PunishFactory().removePunishment(it.id)
                        player.send(Messages().get("punishRepealed").replaceInfo(it))

                        punishments.remove(it)

                        player.closeInventory()
                        if (punishments.isNotEmpty()) player.openRevokeMenu(punishments)
                    }
                }
            }

            onPageChange {
                updateSlot(previousPageSlot, player)
                updateSlot(nextPageSlot, player)
            }
        }

        fun MenuPlayerInventorySlot.showItemWhenHasPage(type: String, hasPageLogic: () -> Boolean) {
            showingItem = if (hasPageLogic()) when (type) {
                "next" -> nextItemSlot.item
                "previous" -> previousItemSlot.item
                else -> null
            }
            else null
        }

        nextItemSlot.onRender { showItemWhenHasPage("next") { pagination.hasNextPage(player) } }
        nextItemSlot.onUpdate { showItemWhenHasPage("next") { pagination.hasNextPage(player) } }
        previousItemSlot.onRender { showItemWhenHasPage("previous") { pagination.hasPreviousPage(player) } }
        previousItemSlot.onUpdate { showItemWhenHasPage("previous") { pagination.hasPreviousPage(player) } }
    }.openToPlayer(this)
}

fun buildItem(location: String, skullOwnerReplace: String? = null) = if (!Main.settings.getString("$location.skullOwner").startsWith("http")) ItemBuilder(ItemStack(Main.settings.getInt("$location.id")))
    .setDurability(Main.settings.getInt("$location.data"))
    .setSkullOwner(skullOwnerReplace ?: Main.settings.getString("$location.skullOwner"))
    .setName(Main.settings.getString("$location.name").replaceColor())
    .setLore(Main.settings.getStringList("$location.lore").replaceColor()).build()
else ItemBuilder(SkullUtils().getSkull(Main.settings.getString("$location.skullOwner")))
    .setName(Main.settings.getString("$location.name").replaceColor())
    .setLore(Main.settings.getStringList("$location.lore").replaceColor()).build()