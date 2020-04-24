package stanic.stbans.factory

import stanic.stbans.Main
import stanic.stbans.factory.model.Punishment

/**
 * This class is for making changes to plugin cache and save
 * and also to do some checks
 */

class PunishFactory {

    /**
     * This function is for verify if the player has a punishment
     *
     * @param [nick] return the player nick
     *
     * @return is a boolean to check if has a punishment
     */
    fun hasPunishment(nick: String): Boolean {
        Punishment.all.forEach { p ->
            if (p.nick == nick && p.isActive) {
                return true
            }
        }
        return false
    }

    /**
     * This function is for verify if the address has a punishment
     *
     * @param [address] return the address
     *
     * @return is a boolean to check if has a punishment
     */
    fun hasPunishmentAddress(address: String): Punishment? {
        Punishment.all.forEach { p ->
            if (p.address != null && p.address == address && p.isActive) {
                return p
            }
        }
        return null
    }

    /**
     * This function is to get the player punish
     *
     *
     */
    fun getPunish(nick: String, type: String): Punishment? {
        Punishment.all.forEach { p ->
            if (p.nick == nick && p.isActive && p.type.contains(type)) {
                return p
            }
        }
        return null
    }

    /**
     * This function is to save a punishment in database
     *
     *  @param [p] return the punishment
     */
    fun savePunish(p: Punishment) {
        val c = Main.instance.db
        c.open()

        val rs = c.statement!!.executeQuery("select * from ${Main.table} where id='${p.id}'")
        if (rs.next()) c.statement!!.executeUpdate("update ${Main.table} set player='${p.nick}', staff='${p.staff}', type='${p.type}', reason='${p.reason}', date='${p.date}', hour='${p.hour}', time='${p.time}', id='${p.id}', address='${p.address}' active='${if (p.isActive) 1 else 0}'")
        else c.statement!!.execute("insert into ${Main.table} (player, staff, type, reason, date, hour, time, id, address, active) values ('${p.nick}', '${p.staff}', '${p.type}', '${p.reason}', '${p.date}', '${p.hour}', '${p.time}', '${p.id}', '${p.address}', '${if (p.isActive) 1 else 0}')")
        c.close()
    }

    /**
     * This function is to deactivate the punishment
     *
     * @param [id] return the punishment id
     */
    fun removePunishment(id: Int) {
        val p = getPunishByID(id)!!

        val c = Main.instance.db
        c.open()
        val rs = c.statement!!.executeQuery("select * from ${Main.table} where id='$id'")
        if (rs.next()) c.statement!!.execute("delete from ${Main.table} where id='$id'")
        c.close()

        p.isActive = false
        p.time = null

        savePunish(p)
    }

    /**
     *
     * This function is to return the historic of a player
     *
     * @param [nick] return the nick of the player to search historic
     * @param [filter] is to filter the punishment type (If is active or not)
     *
     * @return the historic of the player
     */
    fun getHistoric(nick: String, filter: Int = 0): List<Punishment> {
        val history = ArrayList<Punishment>()
        Punishment.all.forEach { p ->
            if (p.nick == nick) {
                when {
                    filter == 0 -> history.add(p)
                    filter == 1 && p.isActive -> history.add(p)
                }
            }
        }

        return history
    }

    /**
     *
     * This function is to search a punishment by id
     *
     * @param [id] return the id to search a punishment
     *
     * @return the punishment
     */
    fun getPunishByID(id: Int): Punishment? {
        Punishment.all.forEach { p ->
            if (p.id == id) {
                return p
            }
        }
        return null
    }

    /**
     *
     * This function is to search a punishment by nick
     *
     * @param [nick] return the nick to search a punishment
     *
     * @return return the punishments list (A player may have more than one punishment)
     */
    fun getPunishByNick(nick: String): List<Punishment> {
        val punishments = ArrayList<Punishment>()
        Punishment.all.forEach { p ->
            if (p.nick == nick) {
                punishments.add(p)
            }
        }
        return punishments
    }

}