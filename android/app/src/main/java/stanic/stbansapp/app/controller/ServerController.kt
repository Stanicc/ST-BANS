package stanic.stbansapp.app.controller

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.json.simple.JSONObject
import stanic.stbansapp.app.MainApp
import stanic.stbansapp.app.model.Punishment
import java.io.IOException

/**
 *
 * This class is to management the server requests
 *
 */

@Suppress("NAME_SHADOWING")
class ServerController {

    /**
     * Interface to return the response methods
     */
    interface ServerResponse {
        /**
         *
         * Function to return the response
         *
         * @param [body] return the response body
         *
         */
        fun onResponse(body: String)

        /**
         *
         * Function to return the response
         *
         * @param [punishments] return the punishments list
         *
         */
        fun onResponseList(punishments: List<Punishment>)

        /**
         *
         * Function to return if the response as failed
         *
         * @param [error] return the error message
         *
         */
        fun onFailure(error: String)
    }

    /**
     *
     * Function to verify and login the server and the token in app
     *
     * @param [serverAddress] return the server address
     * @param [token] return the acess token to check the permissions
     * @param [serverResponse] return the response methods by interface "ServerResponse"
     *
     */
    fun requestLogin(serverAddress: String, token: String, serverResponse: ServerResponse) {
        val client = OkHttpClient()

        val builder = HttpUrl.parse("http://$serverAddress:4567/auth/$token")!!.newBuilder()
        val url = builder.build().toString()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                serverResponse.onFailure(e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()

                val typeToken = object : TypeToken<JSONObject>() {}.type
                val responseBody =
                    gson.fromJson<JSONObject>(response.body()!!.string(), typeToken)

                val validated = responseBody["validated"].toString()
                val permissionLevel = responseBody["permissionLevel"].toString()

                serverResponse.onResponse(
                    "$validated//${permissionLevel.replace(
                        "\"",
                        ""
                    ).replace(".0", "")}"
                )
            }
        })
    }

    /**
     * Function to obtain the information of a punishment
     *
     * @param [id] return the punishment id
     * This is generic because id can be a nick
     *
     * @param [serverResponse] return the response methods by interface "ServerResponse"
     *
     */
    fun <T> getPunish(id: T, typeRequest: String, serverResponse: ServerResponse) {
        val client = OkHttpClient()

        val builder =
            HttpUrl.parse("http://${MainApp.instance.user[0].serveraddress}:4567/punishments/id/$id")!!.newBuilder()

        val url = builder.build().toString()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                serverResponse.onFailure(e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()

                val body = response.body()!!.string()

                if (body == "notFound") {
                    serverResponse.onFailure("404")
                    return
                }

                when (typeRequest) {
                    "normal" -> {
                        val typeToken = object : TypeToken<JSONObject>() {}.type
                        val responseBody =
                            gson.fromJson<JSONObject>(body, typeToken)

                        val punishment = Punishment(
                            responseBody["nick"].toString(),
                            responseBody["staff"].toString(),
                            responseBody["type"].toString(),
                            responseBody["reason"].toString(),
                            responseBody["date"].toString(),
                            responseBody["hour"].toString(),
                            responseBody["time"].toString(),
                            responseBody["id"].toString().replace("\"", "").replace(
                                ".0",
                                ""
                            ).toInt(),
                            responseBody["isActive"].toString() == "true"
                        )
                        serverResponse.onResponse("${punishment.nick}//${punishment.staff}//${punishment.type}//${punishment.reason}//${punishment.date}//${punishment.time}//${punishment.id}//${if (punishment.isActive) "Ativa" else "Expirada"}")
                    }
                    "list" -> {
                        val punishments = ArrayList<Punishment>()

                        val typeToken = object : TypeToken<List<JSONObject>>() {}.type
                        val responseBody =
                            gson.fromJson<List<JSONObject>>(body, typeToken)

                        responseBody.forEach {
                            val punishment = Punishment(
                                it["nick"].toString(),
                                it["staffer"].toString(),
                                it["type"].toString(),
                                it["reason"].toString(),
                                it["date"].toString(),
                                it["hour"].toString(),
                                it["time"].toString(),
                                it["id"].toString().replace("\"", "").replace(
                                    ".0",
                                    ""
                                ).toInt(),
                                it["isActive"].toString() == "true"
                            )
                            punishments.add(punishment)
                        }

                        serverResponse.onResponseList(punishments)
                    }
                }
            }
        })

    }

    /**
     * Function to get the all punishments
     *
     * @param [serverResponse] return the response methods by interface "ServerResponse"
     *
     */
    fun getPunishmentsList(serverResponse: ServerResponse) {
        val client = OkHttpClient()

        val builder =
            HttpUrl.parse("http://${MainApp.instance.user[0].serveraddress}:4567/punishments/list")!!.newBuilder()

        val url = builder.build().toString()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                serverResponse.onFailure(e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()

                val punishments = ArrayList<Punishment>()

                val typeToken = object : TypeToken<List<JSONObject>>() {}.type

                val body = response.body()!!.string()

                val responseBody =
                    gson.fromJson<List<JSONObject>>(body, typeToken)

                responseBody.forEach {
                    val punishment = Punishment(
                        it["nick"].toString(),
                        it["staff"].toString(),
                        it["type"].toString(),
                        it["reason"].toString(),
                        it["date"].toString(),
                        it["hour"].toString(),
                        it["time"].toString(),
                        it["id"].toString().replace("\"", "").replace(
                            ".0",
                            ""
                        ).toInt(),
                        it["isActive"].toString() == "true"
                    )
                    punishments.add(punishment)
                }

                serverResponse.onResponseList(punishments)
            }
        })
    }

    /**
     *
     * Function to revoke a punish
     *
     * @param [id] return the punishment id
     * @param [serverResponse] return the response methods by interface "ServerResponse"
     *
     */
    fun sendRevoke(id: Int, serverResponse: ServerResponse) {
        val mediaType = MediaType.parse("application/json")
        val params = JSONObject()
        params["id"] = id

        val client = OkHttpClient()

        val body = RequestBody.create(mediaType, params.toString())

        val builder =
            HttpUrl.parse("http://${MainApp.instance.user[0].serveraddress}:4567/revoke")!!.newBuilder()
        builder.addQueryParameter("id", "$id")

        val url = builder.build().toString()

        val request = okhttp3.Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                serverResponse.onFailure(e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                serverResponse.onResponse(response.body()!!.string().replace("\"", ""))
            }
        })
    }

    /**
     *
     * Function to make a punish
     *
     * @param [nick] return the banned player nick
     * @param [staffer] return the staffer nick
     * @param [reason] return the reason for the punishment
     * @param [type] return the type of the punishment
     *
     * @param [serverResponse] return the response methods by interface "ServerResponse"
     *
     */
    fun sendPunish(
        nick: String,
        staffer: String,
        reason: String,
        type: String,
        serverResponse: ServerResponse
    ) {
        val mediaType = MediaType.parse("application/json")
        val params = JSONObject()
        params["nick"] = nick
        params["staffer"] = staffer
        params["type"] = type
        params["reason"] = reason

        val client = OkHttpClient()

        val body = RequestBody.create(mediaType, params.toString())

        val builder =
            HttpUrl.parse("http://${MainApp.instance.user[0].serveraddress}:4567/punish/create")!!.newBuilder()
        builder.addQueryParameter("nick", nick)
        builder.addQueryParameter("staffer", staffer)
        builder.addQueryParameter("type", type)
        builder.addQueryParameter("reason", reason)

        val url = builder.build().toString()

        val request = okhttp3.Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                serverResponse.onFailure(e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                serverResponse.onResponse(response.body()!!.string().replace("\"", ""))
            }
        })
    }

    /**
     *
     * Function to make a temporarily punish
     *
     * @param [nick] return the banned player nick
     * @param [staffer] return the staffer nick
     * @param [time] return the time to punish
     * @param [timeUnit] return the time type to punish
     * @param [reason] return the reason for the punishment
     * @param [type] return the type of the punishment
     *
     * @param [serverResponse] return the response methods by interface "ServerResponse"
     *
     */
    fun sendTempPunish(
        nick: String,
        staffer: String,
        time: Int,
        timeUnit: String,
        reason: String,
        type: String,
        serverResponse: ServerResponse
    ) {
        val mediaType = MediaType.parse("application/json")
        val params = JSONObject()
        params["nick"] = nick
        params["staffer"] = staffer
        params["type"] = type
        params["time"] = time
        params["timeUnit"] = timeUnit
        params["reason"] = reason

        val client = OkHttpClient()

        val body = RequestBody.create(mediaType, params.toString())

        val builder =
            HttpUrl.parse("http://${MainApp.instance.user[0].serveraddress}:4567/temppunish/create")!!.newBuilder()
        builder.addQueryParameter("nick", nick)
        builder.addQueryParameter("staffer", staffer)
        builder.addQueryParameter("type", type)
        builder.addQueryParameter("time", time.toString())
        builder.addQueryParameter("timeUnit", timeUnit)
        builder.addQueryParameter("reason", reason)

        val url = builder.build().toString()

        val request = okhttp3.Request.Builder()
            .url(url)
            .post(body)
            .build()

        Log.d("AAA", request.url().toString())
        Log.d("AAA", request.body().toString())

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                serverResponse.onFailure(e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                serverResponse.onResponse(response.body()!!.string().replace("\"", ""))
            }
        })
    }

}