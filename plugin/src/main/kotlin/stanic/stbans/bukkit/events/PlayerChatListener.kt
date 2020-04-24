package stanic.stbans.bukkit.events

import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import stanic.stbans.Main
import stanic.stbans.factory.PunishFactory
import stanic.stbans.utils.Messages
import stanic.stbans.utils.replaceInfo
import stanic.stutils.bukkit.event.event
import stanic.stutils.bukkit.message.send

class PlayerChatListener {

    fun onChat(m: Main) = m.event<AsyncPlayerChatEvent> { event ->
        val player = event.player

        if (PunishFactory().hasPunishment(player.name)) {
            val info = PunishFactory().getPunish(player.name, "Mute") ?: return@event

            info.time?.run {
                if (System.currentTimeMillis() >= this) {
                    PunishFactory().removePunishment(info.id)
                    player.send(Messages().get("punishRepealedChat").replaceInfo(info))
                    event.isCancelled = true
                    return@event
                }
            }

            player.send(Messages().get("muteMessage").replaceInfo(info))
            event.isCancelled = true
        }
    }

    fun onCommand(m: Main) = m.event<PlayerCommandPreprocessEvent> { event ->
        val player = event.player
        val command = event.message

        if (PunishFactory().hasPunishment(player.name)) {
            if (Main.settings.getStringList("Config.blockedCommands").contains(command)) {
                val info = PunishFactory().getPunish(player.name, "Mute") ?: return@event

                info.time?.run {
                    if (System.currentTimeMillis() >= this) {
                        PunishFactory().removePunishment(info.id)
                        player.send(Messages().get("punishRepealedChat").replaceInfo(info))
                        event.isCancelled = true
                        return@event
                    }
                }

                player.send(Messages().get("muteMessage").replaceInfo(info))
                event.isCancelled = true
            }
        } else if (player.address != null && PunishFactory().hasPunishmentAddress(player.address.hostString) != null) {
            if (Main.settings.getStringList("Config.blockedCommands").contains(command)) {
                val info = PunishFactory().hasPunishmentAddress(player.address.hostString)!!

                info.time?.run {
                    if (System.currentTimeMillis() >= this) {
                        PunishFactory().removePunishment(info.id)
                        player.send(Messages().get("punishRepealedChat").replaceInfo(info))
                        event.isCancelled = true
                        return@event
                    }
                }

                player.send(Messages().get("muteMessage").replaceInfo(info))
                event.isCancelled = true
            }
        }
    }

}