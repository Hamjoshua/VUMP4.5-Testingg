package org.example.data.sqlite_repos

import java.sql.*;

open class SqliteRepoBase(dbFilePath: String) {
    private lateinit var _connection : Connection

    init {
        val fullPath = "jdbc:sqlite:${dbFilePath}"
        _connection = DriverManager.getConnection(fullPath)
    }
}