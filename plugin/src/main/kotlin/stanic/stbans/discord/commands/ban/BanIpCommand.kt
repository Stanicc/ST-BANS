package stanic.stbans.discord.commands.ban

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.internal.utils.PermissionUtil
import org.bukkit.Bukkit
import stanic.stbans.Main
import stanic.stbans.controller.PunishController
import stanic.stbans.discord.utils.command
import stanic.stbans.discord.utils.get
import stanic.stbans.discord.utils.reply
import stanic.stbans.factory.model.Punishment
import stanic.stbans.utils.TimeUtils
import stanic.stbans.utils.replaceInfo

fun JDA.registerBanIpCommand() = command("banip") {
    if (!PermissionUtil.checkPermission(member, Permission.BAN_MEMBERS)) channel.reply("noPermDiscord".get().replace("{mention}", author.asMention))
    else {
        val args = message.contentRaw.split(" ")
        if (args.size < 2) {
            channel.reply("usageBanIpDiscord".get().replace("{mention}", author.asMention))
            return@command
        }

        var reason = ""
        (2 until args.size).forEach { i ->
            reason = "$reason${args[i]} "
        }

        val id = Main.instance.punishment.size + 1

        val punish = Punishment(
            args[1],
            author.name,
            "BanIp",
            reason,
            TimeUtils.getDate(),
            TimeUtils.getHour(),
            null,
            id,
            Bukkit.getOfflinePlayer(args[1])?.player?.address?.hostString,
            isActive = false
        )

        Main.instance.server.scheduler.runTask(Main.instance) {
            Main.instance.punishment[id] = punish
            Main.instance.playerInfo.remove(args[1])

            PunishController().applyPunishment(args[1], id)
        }

        val embed = EmbedBuilder()
            .setTitle("punishmentAppliedTitle".get().replaceInfo(punish).replace("{mention}", author.asMention))
            .setDescription("punishmentAppliedDescription".get().replaceInfo(punish).replace("{mention}", author.asMention))
        channel.reply(embed.build())
    }
}