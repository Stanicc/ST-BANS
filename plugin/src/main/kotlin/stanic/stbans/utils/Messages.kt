package stanic.stbans.utils

import stanic.stbans.Main
import stanic.stbans.factory.model.Punishment
import stanic.stutils.bukkit.message.replaceColor

class Messages {

    fun get(msg: String) = Main.settings.getString("Messages.$msg").replaceColor().replace("@n", "\n")

}

fun String.replaceInfo(info: Punishment) = this.run {
    replace("{reason}", info.reason)
        .replace("{active}", if (info.isActive) Main.settings.getString("Config.isActive") else Main.settings.getString("Config.isNotActive"))
        .replace("{nick}", info.nick)
        .replace("{type}", info.type)
        .replace("{staff}", info.staff)
        .replace("{date}", info.date)
        .replace("{hour}", info.hour)
        .replace("{address}", if (info.address != null) info.address!! else "N/A")
        .replace(
            "{time}",
            when {
                info.time != null && info.isActive -> TimeUtils().getTime(info.time!! - System.currentTimeMillis())
                info.time == null && info.isActive -> Main.settings.getString(
                    "Config.timeFormat.permanent"
                )
                else -> Main.settings.getString("Config.finalized")
            }
        )
        .replace("{id}", info.id.toString())
}