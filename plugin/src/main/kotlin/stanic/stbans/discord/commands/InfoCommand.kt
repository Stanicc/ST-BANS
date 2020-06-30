package stanic.stbans.discord.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import stanic.stbans.discord.utils.command
import stanic.stbans.discord.utils.get
import stanic.stbans.discord.utils.reply
import stanic.stbans.factory.PunishFactory
import stanic.stbans.utils.replaceInfo

fun JDA.registerInfoCommand() = command("info") {
    val args = message.contentRaw.split(" ")
    if (args.size == 1) {
        channel.reply("usageInfoDiscord".get().replace("{mention}", author.asMention))
        return@command
    }

    try {
        if (PunishFactory().getPunishByID(Integer.parseInt(args[1])) != null) {
            val punishment = PunishFactory().getPunishByID(Integer.parseInt(args[1]))!!

            val embed = EmbedBuilder()
                .setTitle("punishmentInfoTitle".get().replaceInfo(punishment).replace("{mention}", author.asMention))
                .setDescription("punishmentInfoDescription".get().replaceInfo(punishment).replace("{mention}", author.asMention))
            channel.reply(embed.build())
        } else channel.reply("punishNotFoundDiscord".get().replace("{mention}", author.asMention))
    } catch (e: NumberFormatException) {
        channel.reply("onlyNumbersDiscord".get().replace("{mention}", author.asMention))
    }
}