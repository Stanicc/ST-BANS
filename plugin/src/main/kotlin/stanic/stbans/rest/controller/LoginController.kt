package stanic.stbans.rest.controller

import stanic.stbans.Main

class LoginController {

    fun verifyToken(token: String): Login {
        return if (Main.token["Tokens.$token"] != null) {
            Login(true, Main.token.getInt("Tokens.$token"))
        } else {
            Login(false, 0)
        }
    }

    inner class Login(
        var validated: Boolean,
        var permissionLevel: Int
    )

}