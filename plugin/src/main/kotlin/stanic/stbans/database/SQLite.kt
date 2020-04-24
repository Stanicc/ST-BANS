package stanic.stbans.database

import stanic.stbans.Main
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

/**
 *
 * SQLite database
 *
 */

class SQLite(private val m: Main) : IDatabase {

    override val type: String
        get() = "SQLite"

    override var connection: Connection? = null

    override var statement: Statement? = null

    override fun open(): Boolean {
        try {
            Class.forName("org.sqlite.JDBC")
            if (this.connection == null) {
                this.connection = DriverManager
                    .getConnection("jdbc:sqlite:" + m.dataFolder.path + "/database.db")
            }
            if (this.statement == null && this.connection != null) {
                this.statement = this.connection!!.createStatement()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return this.connection()
    }

    override fun connection(): Boolean {
        return this.connection != null
    }

    override fun close(): Boolean {
        if (connection()) {
            try {
                if (this.statement != null)
                    this.statement!!.close()
                if (this.connection != null)
                    this.connection!!.close()

                this.statement = null
                this.connection = null
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }
        return connection()
    }

}
