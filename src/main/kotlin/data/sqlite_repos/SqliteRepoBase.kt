package org.example.AccessControl.data.sqlite_repos

import org.springframework.beans.factory.annotation.Value
import java.sql.*
import kotlin.io.path.exists
import kotlin.io.path.Path
import org.springframework.stereotype.Repository

@Repository
open class SqliteRepoBase(@Value("\${app.db.path}") dbFilePath: String = "") {
    protected lateinit var _connection: Connection

    init {
        // проверка на нормальное подключение к БД
        val dbPath = Path(dbFilePath)
        if (!dbPath.exists()){
            throw SQLException("Not connected to db at address ${dbFilePath}")
        }

        val fullPath = "jdbc:sqlite:${dbFilePath}"
        _connection = DriverManager.getConnection(fullPath)
    }
}