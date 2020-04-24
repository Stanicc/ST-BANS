package stanic.stbans.controller

import org.bukkit.Bukkit
import stanic.stbans.Main
import stanic.stbans.discord.sendToDiscord
import stanic.stbans.factory.PunishFactory
import stanic.stbans.utils.Messages
import stanic.stbans.utils.replaceInfo
import stanic.stutils.bukkit.message.send
import java.util.concurrent.TimeUnit

/**
 * This class has the responsibility of control the punishments
 */

class PunishController {

    /**
     * Method to apply the punish
     *
     * @param [p] return the player nick
     * @param [id] return the punish id
     * @param [typeUnit] return the timeUnit | If the punish is permanent make the default null
     */
    fun applyPunishment(p: String, id: Int, typeUnit: TimeUnit? = null) {
        val info = Main.instance.punishment[id]!!
        val time = if (info.time != null) System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(info.time!!, typeUnit)  else null

        info.time = time
        info.isActive = true

        Bukkit.getPlayer(p)?.run {
            if (info.type.contains("Ban")) {
                kickPlayer(Messages().get("kickBanned").replaceInfo(info))
            } else {
                send(Messages().get("muteMessage").replaceInfo(info))
            }
        }

        Bukkit.getPlayer(info.staff)?.run {
            send(Messages().get("punishmentAppliedStaff").replaceInfo(info))
        }

        if (Main.settings.getBoolean("Config.alertPunishBroadcast")) {
            Bukkit.getOnlinePlayers().forEach {
                it.send(
                    Messages().get("playerPunishBroadcast").replaceInfo(info)
                )
            }
        }

        val message =
            "${Main.settings.getString("Discord.channels.punishMessage.title").replace("@n", "\n").replaceInfo(info)}[/title/][/body/]${Main.settings.getString(
                "Discord.channels.punishMessage.body"
            ).replace("@n", "\n").replaceInfo(info)}"

        message.sendToDiscord(Main.settings.getString("Discord.channels.punishChannel"))

        PunishFactory().savePunish(info)
    }

}