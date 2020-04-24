package stanic.stbans.factory.model

import stanic.stbans.Main
import java.util.stream.Collectors

class PlayerInfo(
    var player: String,
    var warns: Int,
    var reports: ArrayList<String>
) {

    companion object {
        val all: List<PlayerInfo>
            get() = Main.instance.playerInfo.values.stream().collect(Collectors.toList())
    }

}