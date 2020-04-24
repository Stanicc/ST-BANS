package stanic.stbansapp.app

import android.util.SparseArray
import androidx.core.util.set
import stanic.stbansapp.app.controller.ServerController
import stanic.stbansapp.app.model.Punishment
import stanic.stbansapp.app.model.User

class MainApp {

    val user = SparseArray<User>()

    fun login(address: String, token: String) {
        instance = this

        val userModel = User(address, token, 0, false)

        ServerController().requestLogin(address, token, object : ServerController.ServerResponse {
            override fun onFailure(error: String) {
                user[0] = userModel
            }

            override fun onResponse(body: String) {
                if (body.split("//")[0].toBoolean()) {
                    userModel.permission = body.split("//")[1].toInt()
                    userModel.login = true
                    user[0] = userModel
                } else {
                    userModel.permission = body.split("//")[1].toInt()
                    userModel.login = false
                    user[0] = userModel
                }
            }
            override fun onResponseList(punishments: List<Punishment>) {
                TODO("not implemented")
            }
        })

        user[0] = userModel
    }

    companion object {
        lateinit var instance: MainApp
            private set
    }

}