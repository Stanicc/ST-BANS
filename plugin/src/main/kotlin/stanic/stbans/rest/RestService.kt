package stanic.stbans.rest

import com.google.gson.GsonBuilder
import org.bukkit.Bukkit
import spark.kotlin.Http
import spark.kotlin.ignite
import stanic.stbans.Main
import stanic.stbans.controller.PunishController
import stanic.stbans.factory.PunishFactory
import stanic.stbans.factory.model.Punishment
import stanic.stbans.rest.controller.LoginController
import stanic.stbans.rest.model.PunishmentModel
import stanic.stbans.utils.TimeUtils
import java.util.concurrent.TimeUnit
import kotlin.collections.set

/**
 * This class has the function of managing the rest api to interconnect the application with the plugin.
*/

@Suppress("KDocUnresolvedReference")
class RestService {

    /**
     * This companion contains the var of the http client
     */
    companion object {
        lateinit var http: Http
            private set
    }

    /**
     * Method to start the rest api and register the events
     */
    fun startService() {
        http = ignite()
        http.port(Main.settings.getInt("Config.appConnectPort"))

        http.run {
            /**
             * Event to get information about an punishment by id
             *
             * @param [id] return the id of a punish
             *
             * @exception [NumberFormatException] is for to separate the punish by id and nick
             */
            get("/punishments/id/:id") {
                try {
                    val info = PunishFactory().getPunishByID(params(":id").toInt())

                    if (info != null) {
                        val punishment = PunishmentModel(
                            info.nick,
                            info.staff,
                            info.type,
                            info.reason,
                            info.date,
                            info.hour,
                            when {
                                info.time != null && info.isActive -> TimeUtils().getTime(info.time!! - System.currentTimeMillis())
                                info.time == null && info.isActive -> Main.settings.getString(
                                    "Config.timeFormat.permanent"
                                )
                                else -> Main.settings.getString("Config.finalized")
                            },
                            info.id,
                            info.isActive
                        )

                        GsonBuilder().create().toJson(punishment).apply {
                            response.status(200)
                        }
                    } else {
                        "notFound".apply {
                            response.status(404)
                        }
                    }
                } catch (e: NumberFormatException) {
                    val punishments = ArrayList<PunishmentModel>()
                    val punishByNick = PunishFactory().getPunishByNick(params(":id"))

                    if (punishByNick.isNotEmpty()) {
                        punishByNick.forEach {
                            punishments.add(
                                PunishmentModel(
                                    it.nick,
                                    it.staff,
                                    it.type,
                                    it.reason,
                                    it.date,
                                    it.hour,
                                    when {
                                        it.time != null && it.isActive -> TimeUtils().getTime(it.time!! - System.currentTimeMillis())
                                        it.time == null && it.isActive -> Main.settings.getString(
                                            "Config.timeFormat.permanent"
                                        )
                                        else -> Main.settings.getString("Config.finalized")
                                    },
                                    it.id,
                                    it.isActive
                                )
                            )
                        }

                        GsonBuilder().create().toJson(punishments).apply {
                            response.status(200)
                        }
                    } else {
                        "notFound".apply {
                            response.status(404)
                        }
                    }
                }
            }

            /**
             * Event to create a permanent punish
             *
             * In this event we wait the params [nick, staffer and reason] to apply the punishment
             */
            post("/punish/create") {
                //Val to get the punishment ID
                val id = Main.instance.punishment.size + 1
                //Val to get the param "nick"
                val nick = queryParams("nick")

                //Creating the punish model and put he to punishments hashmap
                val punish = Punishment(
                    nick,
                    queryParams("staffer"),
                    queryParams("type"),
                    queryParams("reason"),
                    TimeUtils.getDate(),
                    TimeUtils.getHour(),
                    null,
                    id,
                    isActive = false
                )
                Main.instance.punishment[id] = punish

                //Running the method to apply punishment in another thread to avoid bugs
                Bukkit.getServer().scheduler.runTask(Main.instance) {
                    PunishController().applyPunishment(nick, id)
                }

                //Call response to "OK" and redirecting to punishment info
                GsonBuilder().create().toJson("$id").apply {
                    response.status(201)
                }

            }

            /**
             * Event to create a temp punish
             *
             * In this event we wait the params [nick, staffer, time, timeUnit and reason] to apply the punishment
             */
            post("/temppunish/create") {
                //Val to get the punishment ID
                val id = Main.instance.punishment.size + 1
                //Val to get the param "nick"
                val nick = queryParams("nick")
                //Val to get the param "timeUnit"
                val timeUnit = queryParams("timeUnit")

                //Creating the punish model and put he to punishments hashmap
                val punish = Punishment(
                    nick,
                    queryParams("staffer"),
                    queryParams("type"),
                    queryParams("reason"),
                    TimeUtils.getDate(),
                    TimeUtils.getHour(),
                    queryParams("time").toLong(),
                    id,
                    isActive = false
                )
                Main.instance.punishment[id] = punish

                //Running the method to apply punishment in another thread to avoid bugs
                Bukkit.getServer().scheduler.runTask(Main.instance) {
                    PunishController().applyPunishment(
                        nick, id, when (timeUnit) {
                            "s" -> TimeUnit.SECONDS
                            "m" -> TimeUnit.MINUTES
                            "h" -> TimeUnit.HOURS
                            "d" -> TimeUnit.DAYS
                            else -> TimeUnit.DAYS
                        }
                    )
                }

                //Call response to "OK" and redirecting to punishment info
                GsonBuilder().create().toJson("$id").apply {
                    response.status(201)
                }
            }

            /**
             *
             * This event is to revoke a punishment
             *
             * In this event we wait the param "id" to revoke
             *
             */
            post("/revoke") {
                try {
                    val id = queryParams("id").toInt()

                    Bukkit.getServer().scheduler.runTask(Main.instance) {
                        PunishFactory().removePunishment(id)
                    }

                    response.status(200)
                } catch (e: NumberFormatException) {
                    response.status(400)
                }
            }

            /**
             *
             * This event return the list of all punishments order by data
             *
             */
            get("/punishments/list") {
                val punishments = ArrayList<PunishmentModel>()

                Punishment.all.forEach {
                    punishments.add(
                        PunishmentModel(
                            it.nick,
                            it.staff,
                            it.type,
                            it.reason,
                            it.date,
                            it.hour,
                            when {
                                it.time != null && it.isActive -> TimeUtils().getTime(it.time!! - System.currentTimeMillis())
                                it.time == null && it.isActive -> Main.settings.getString(
                                    "Config.timeFormat.permanent"
                                )
                                else -> Main.settings.getString("Config.finalized")
                            },
                            it.id,
                            it.isActive
                        )
                    )
                }

                GsonBuilder().create().toJson(punishments).apply {
                    if (this.isNotEmpty()) response.status(200)
                    else response.status(404)
                }
            }

            get("/auth/:token") {
                val login = LoginController().verifyToken(params(":token"))

                GsonBuilder().create().toJson(login).apply {
                    if (login.validated && Main.settings.getBoolean("Config.enableApp")) response.status(202)
                    else response.status(406)
                }
            }
        }
    }

}