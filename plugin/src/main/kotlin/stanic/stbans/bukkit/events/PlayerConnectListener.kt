package stanic.stbans.bukkit.events

import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import stanic.stbans.Main
import stanic.stbans.factory.PunishFactory
import stanic.stbans.factory.model.PlayerInfo
import stanic.stbans.utils.Messages
import stanic.stbans.utils.replaceInfo
import stanic.stutils.bukkit.event.event

class PlayerConnectListener {

    fun onConnect(m: Main) = m.event<PlayerJoinEvent> { event ->
        val player = event.player

        if (!Main.instance.playerInfo.containsKey(player.name)) {
            val playerInfo = PlayerInfo(player.name, 0, ArrayList())
            Main.instance.playerInfo[player.name] = playerInfo
        }
    }

    fun onLogin(m: Main) = m.event<PlayerLoginEvent> { event ->
        val player = event.player

        if (PunishFactory().hasPunishment(player.name)) {
            val info = PunishFactory().getPunish(player.name, "Ban") ?: return@event

            info.time?.run {
                if (System.currentTimeMillis() >= this) {
                    PunishFactory().removePunishment(info.id)
                    event.result = PlayerLoginEvent.Result.KICK_BANNED
                    event.kickMessage = Messages().get("punishRepealedKick").replaceInfo(info)
                    return@event
                }
            }

            event.result = PlayerLoginEvent.Result.KICK_BANNED
            event.kickMessage = Messages().get("kickBanned").replaceInfo(info)
        } else if (player.address != null && PunishFactory().hasPunishmentAddress(player.address.hostString) != null) {
            val info = PunishFactory().hasPunishmentAddress(player.address.hostString)!!

            info.time?.run {
                if (System.currentTimeMillis() >= this) {
                    PunishFactory().removePunishment(info.id)
                    event.result = PlayerLoginEvent.Result.KICK_BANNED
                    event.kickMessage = Messages().get("punishRepealedKick").replaceInfo(info)
                    return@event
                }
            }

            event.result = PlayerLoginEvent.Result.KICK_BANNED
            event.kickMessage = Messages().get("kickBanned").replaceInfo(info)
        }
    }

}