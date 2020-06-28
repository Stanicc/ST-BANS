package stanic.stbans.utils

import stanic.stbans.Main
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeUtils {

    private val secondsString = Main.settings.getString("Config.timeFormat.seconds")!!
    private val minutesString = Main.settings.getString("Config.timeFormat.minutes")!!
    private val hoursString = Main.settings.getString("Config.timeFormat.hours")!!
    private val daysString = Main.settings.getString("Config.timeFormat.days")!!

    fun getTime(time: Long): String {
        val varSeconds = (time / 1000L) % 60L
        val varMinutes = (time / 60000L) % 60L
        val varHours = (time / 3600000L) % 24L
        val varDays = time / (60*60*24*1000)

        val seconds = varSeconds.toString().replace("-".toRegex(), "")
        val minutes = varMinutes.toString().replace("-".toRegex(), "")
        val hours = varHours.toString().replace("-".toRegex(), "")
        val days = varDays.toString().replace("-".toRegex(), "")

        if (days == "0" && hours == "0" && minutes == "0") {
            return "$seconds $secondsString"
        }
        if (days == "0" && hours == "0") {
            return "$minutes $minutesString $seconds $secondsString"
        }
        return if (days == "0") {
            "$hours $hoursString $minutes $minutesString"
        } else "$days $daysString $hours $hoursString"
    }

    companion object {

        private val dtf = DateTimeFormatter.ofPattern("EEEE")
        private val now = LocalDateTime.now()

        private val dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        private val now2 = LocalDateTime.now()

        private val dtf3 = DateTimeFormatter.ofPattern("HH:mm")
        private val now3 = LocalDateTime.now()

        private val day = dtf.format(now)
            .replace("Sunday", Main.settings.getString("Config.timeFormat.sunday"))
            .replace("Monday", Main.settings.getString("Config.timeFormat.monday"))
            .replace("Tuesday", Main.settings.getString("Config.timeFormat.tuesday"))
            .replace("Wednesday", Main.settings.getString("Config.timeFormat.wednesday"))
            .replace("Thursday", Main.settings.getString("Config.timeFormat.thursday"))
            .replace("Friday", Main.settings.getString("Config.timeFormat.friday"))
            .replace("Saturday", Main.settings.getString("Config.timeFormat.saturday"))
        private val date = dtf2.format(now2)
        private val hour = dtf3.format(now3)

        fun getHour(): String {
            return hour
        }

        fun getDate(): String {
            return "$day $date"
        }

    }

}

fun String.removeDay() = this.run {
    replace(Main.settings.getString("Config.timeFormat.sunday") + " ", "")
        .replace(Main.settings.getString("Config.timeFormat.monday") + " ", "")
        .replace(Main.settings.getString("Config.timeFormat.tuesday") + " ", "")
        .replace(Main.settings.getString("Config.timeFormat.wednesday") + " ", "")
        .replace(Main.settings.getString("Config.timeFormat.thursday") + " ", "")
        .replace(Main.settings.getString("Config.timeFormat.friday") + " ", "")
        .replace(Main.settings.getString("Config.timeFormat.saturday") + " ", "")
}