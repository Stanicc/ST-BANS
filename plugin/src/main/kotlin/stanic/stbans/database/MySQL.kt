package stanic.stbans.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

/**
 *
 * MySQL Database
 *
 */

class MySQL : IDatabase {

    var hostname = ""
    var database = ""
    var password = ""
    var username = ""
    var port: Int = 0

    override var connection: Connection? = null

    override var statement: Statement? = null

    override val type: String
        get() = "MySQL"

    override fun open(): Boolean {
        try {

            Class.forName("com.mysql.jdbc.Driver")
            if (this.connection == null) {
                this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.hostname + "/" + this.database,
                    this.username, this.password
                )
            }
            if (this.statement == null && this.connection != null) {
                this.statement = this.connection!!.createStatement()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return connection()
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

    override fun connection(): Boolean {
        return this.connection != null
    }

}