package stanic.stbans.database

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.scheduler.BukkitRunnable
import stanic.stbans.Main
import stanic.stbans.Main.Companion.settings
import stanic.stbans.factory.model.PlayerInfo
import stanic.stbans.factory.model.Punishment

/**
 *
 * This class contains the methods to start the database and load data
 *
 */

class Database {

    /**
     *
     * Function to start the database
     *
     */
    fun start() {
        if (settings.getBoolean("Database.mysql")) {
            val hostname = settings.getString("Database.hostname")
            val databaseName = settings.getString("Database.database")
            val username = settings.getString("Database.username")
            val password = settings.getString("Database.password")
            val tableName = settings.getString("Database.table")
            val port = settings.getInt("Database.port")
            val mysql = MySQL()
            mysql.hostname = hostname
            mysql.database = databaseName
            mysql.username = username
            mysql.password = password
            mysql.port = port
            Main.table = tableName
            Main.instance.db = mysql
        } else {
            Main.instance.db = SQLite(Main.instance)
        }

        if (Main.instance.db.open()) {
            Main.instance.db.close()
        } else {
            Main.instance.db = SQLite(Main.instance)
        }

        Main.instance.db.open()
        Main.instance.db.statement!!.execute("CREATE TABLE IF NOT EXISTS ${Main.table} (player text, staff text, type text, reason text, date text, hour text, time long, id int, address text, active int);")
        Main.instance.db.statement!!.execute("CREATE TABLE IF NOT EXISTS reports (player text, reports text);")
        Main.instance.db.close()
    }

    fun loadData() = object : BukkitRunnable() {
        override fun run() {
            val c = Main.instance.db
            c.open()

            val rs = c.statement!!.executeQuery("SELECT * FROM ${Main.table}")
            if (rs.next()) {
                val player = rs.getString("player")
                val staff = rs.getString("staff")
                val type = rs.getString("type")
                val reason = rs.getString("reason")
                val date = rs.getString("date")
                val hour = rs.getString("hour")
                val time: Long? = if (type == "Ban" || type == "Mute") null else rs.getLong("time")
                val id = rs.getInt("id")
                val isActive = rs.getInt("active") == 1

                val punishment = Punishment(
                    player,
                    staff,
                    type,
                    reason,
                    date,
                    hour,
                    time,
                    id,
                    if (type == "BanIp" || type == "TempBanIp" || type == "TempMuteIp" || type == "MuteIp") rs.getString("address") else null,
                    isActive
                )
                Main.instance.punishment[id] = punishment
            }
            rs.close()

            val rsReports = c.statement!!.executeQuery("SELECT * FROM reports")
            if (rsReports.next()) {
                val player = rsReports.getString("player")
                val reports = rsReports.getString("reports")

                val type = object : TypeToken<ArrayList<String>>() {}.type

                val playerInfo = PlayerInfo(player, 0, Gson().fromJson(reports, type))
                Main.instance.playerInfo[player] = playerInfo
            }
            rsReports.close()

            c.close()
            cancel()
        }
    }.runTaskAsynchronously(Main.instance)!!

}