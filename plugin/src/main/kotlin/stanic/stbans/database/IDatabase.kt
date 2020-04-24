package stanic.stbans.database

import java.sql.Connection
import java.sql.Statement

/**
 *
 * IDatabase impl
 *
 */

interface IDatabase {

    val connection: Connection?
    val statement: Statement?

    val type: String

    fun open(): Boolean
    fun close(): Boolean

    fun connection(): Boolean

}