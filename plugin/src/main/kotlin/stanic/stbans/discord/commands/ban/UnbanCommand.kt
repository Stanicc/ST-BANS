package stanic.stbans.discord.commands.ban

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.internal.utils.PermissionUtil
import stanic.stbans.discord.utils.command
import stanic.stbans.discord.utils.get
import stanic.stbans.discord.utils.reply
import stanic.stbans.factory.PunishFactory

fun JDA.registerUnbanCommand() = command("unban") {
    if (!PermissionUtil.checkPermission(member, Permission.BAN_MEMBERS)) channel.reply("noPermDiscord".get().replace("{mention}", author.asMention))
    else {
        val args = message.contentRaw.split(" ")
        if (args.size == 1) {
            channel.reply("usageUnBanDiscord".get().replace("{mention}", author.asMention))
            return@command
        }

        try {
            if (PunishFactory().getPunishByID(Integer.parseInt(args[1])) != null) {
                PunishFactory().removePunishment(Integer.parseInt(args[1])).apply {
                    channel.reply("punishRepealedDiscord".get().replace("{mention}", author.asMention).replace("{id}", args[1]))
                }
            } else channel.reply("punishNotFoundDiscord".get().replace("{mention}", author.asMention))
        } catch (e: NumberFormatException) {
            channel.reply("onlyNumbersDiscord".get().replace("{mention}", author.asMention))
        }
    }
}