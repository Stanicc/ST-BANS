package stanic.stbansapp.app.model

class User(
    var serveraddress: String,
    var token: String,
    var permission: Int,
    var login: Boolean = false
)