package stanic.stbans

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import stanic.stbans.bukkit.commands.ReportCommand
import stanic.stbans.bukkit.commands.WarnCommand
import stanic.stbans.bukkit.commands.ban.*
import stanic.stbans.bukkit.commands.mute.*
import stanic.stbans.bukkit.commands.staff.KickCommand
import stanic.stbans.bukkit.commands.staff.ReportsCommand
import stanic.stbans.bukkit.commands.staff.VerifyCommand
import stanic.stbans.bukkit.events.InventoryInteractListener
import stanic.stbans.bukkit.events.PlayerChatListener
import stanic.stbans.bukkit.events.PlayerConnectListener
import stanic.stbans.database.Database
import stanic.stbans.database.IDatabase
import stanic.stbans.discord.DiscordBot
import stanic.stbans.factory.model.PlayerInfo
import stanic.stbans.factory.model.Punishment
import stanic.stbans.rest.RestService
import stanic.stutils.bukkit.message.send
import java.io.File

class Main: JavaPlugin() {

    val punishment = HashMap<Int, Punishment>()
    val playerInfo = HashMap<String, PlayerInfo>()

    val reportDelay = HashMap<String, Long>()

    lateinit var db: IDatabase

    override fun onLoad() {
        loadSettings()
        RestService().startService()
    }

    override fun onEnable() {
        instance = this

        Database().start()
        Database().loadData()

        loadCommands()
        loadEvents()

        DiscordBot().start()

        if (!settings.getBoolean("Config.enableApp")) RestService.http.stop()

        Bukkit.getConsoleSender().send("§e[ST-BANS] §fAtivado!")
    }

    override fun onDisable() {
        for (info in PlayerInfo.all) {
            db.open()
            val rs = db.statement!!.executeQuery("SELECT * FROM reports WHERE player='${info.player}'")
            if (rs.next()) db.statement!!.executeUpdate("UPDATE reports SET player='${info.player}', reports='${Gson().toJson(info.reports)}'")
            else db.statement!!.execute("INSERT INTO reports (player, reports) VALUES ('${info.player}', '${Gson().toJson(info.reports)}')")
        }
    }

    private fun loadCommands() {
        BanCommand().run(this)
        BanIpCommand().run(this)
        TempBanCommand().run(this)
        TempBanIpCommand().run(this)
        UnbanCommand().run(this)
        MuteCommand().run(this)
        MuteIpCommand().run(this)
        TempMuteCommand().run(this)
        TempMuteIpCommand().run(this)
        UnmuteCommand().run(this)
        ReportsCommand().run(this)
        VerifyCommand().run(this)
        ReportCommand().run(this)
        WarnCommand().run(this)
        KickCommand().run(this)
    }

    private fun loadEvents() {
        PlayerConnectListener().onConnect(this)
        PlayerConnectListener().onLogin(this)
        PlayerChatListener().onChat(this)
        PlayerChatListener().onCommand(this)
        InventoryInteractListener().onInteractInReports(this)
        InventoryInteractListener().onInteractInHistoric(this)
    }

    private fun loadSettings() {
        val tokens = File(dataFolder, "tokens.yml")
        if (!tokens.exists()) {
            tokens.parentFile.mkdirs()
            saveResource("tokens.yml", false)
        }
        token = YamlConfiguration()
        try {
            token.load(tokens)
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }

        sett = File(dataFolder, "settings.yml")
        if (!sett.exists()) {
            sett.parentFile.mkdirs()
            saveResource("settings.yml", false)
        }
        settings = YamlConfiguration()
        try {
            settings.load(sett)
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    companion object {
        var table = "stbans"

        lateinit var instance: Main

        lateinit var settings: FileConfiguration
            internal set
        lateinit var token: FileConfiguration
            internal set
        lateinit var sett: File
    }

}