package stanic.stbans.discord.commands.mute

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.internal.utils.PermissionUtil
import stanic.stbans.Main
import stanic.stbans.controller.PunishController
import stanic.stbans.discord.utils.command
import stanic.stbans.discord.utils.get
import stanic.stbans.discord.utils.reply
import stanic.stbans.factory.model.Punishment
import stanic.stbans.utils.TimeUtils
import stanic.stbans.utils.replaceInfo
import java.util.concurrent.TimeUnit

fun JDA.registerTempMuteCommand() = command("tempmute") {
    if (!PermissionUtil.checkPermission(member, Permission.BAN_MEMBERS)) channel.reply("noPermDiscord".get().replace("{mention}", author.asMention))
    else {
        val args = message.contentRaw.split(" ")
        if (args.size < 4) {
            channel.reply("usageTempMuteDiscord".get().replace("{mention}", author.asMention))
            return@command
        }

        var reason = ""
        (4 until args.size).forEach { i ->
            reason = "$reason${args[i]} "
        }

        val id = Main.instance.punishment.size + 1

        val punish = Punishment(
            args[1],
            author.name,
            "TempMute",
            reason,
            TimeUtils.getDate(),
            TimeUtils.getHour(),
            args[2].toLong(),
            id,
            isActive = false
        )
        Main.instance.punishment[id] = punish
        Main.instance.playerInfo.remove(args[1])

        PunishController().applyPunishment(
            args[1], id, when (args[3]) {
                "s" -> TimeUnit.SECONDS
                "m" -> TimeUnit.MINUTES
                "h" -> TimeUnit.HOURS
                "d" -> TimeUnit.DAYS
                else -> TimeUnit.DAYS
            }
        )

        val embed = EmbedBuilder()
            .setTitle("punishmentAppliedTitle".get().replaceInfo(punish).replace("{mention}", author.asMention))
            .setDescription("punishmentAppliedDescription".get().replaceInfo(punish).replace("{mention}", author.asMention))
        channel.reply(embed.build())
    }
}