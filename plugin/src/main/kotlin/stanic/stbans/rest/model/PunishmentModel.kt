package stanic.stbans.rest.model

class PunishmentModel(
    var nick: String,
    var staff: String,
    var type: String,
    var reason: String,
    var date: String,
    var hour: String,
    var time: String,
    var id: Int,
    var isActive: Boolean
)