package stanic.stbans.factory.model

import stanic.stbans.Main
import java.util.stream.Collectors

class Punishment(
    var nick: String,
    var staff: String,
    var type: String,
    var reason: String,
    var date: String,
    var hour: String,
    var time: Long?,
    var id: Int,
    var address: String? = null,
    var isActive: Boolean
) {

    companion object {
        val all: List<Punishment>
            get() = Main.instance.punishment.values.stream().collect(Collectors.toList())
    }

}