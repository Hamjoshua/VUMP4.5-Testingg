package org.example.AccessControl.data.sqlite_repos

import data.interfaces.IUsersRepo
import org.example.AccessControl.entities.User
import org.springframework.stereotype.Repository

@Repository
class SqliteUsersRepo(dbFilePath: String) : SqliteRepoBase(dbFilePath), IUsersRepo {
    override fun getById(id: Int): User? {
        val sql = "SELECT * FROM users WHERE id = ?"

        _connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, id)
            val result = statement.executeQuery()

            return if (result.next()) {
                User(
                    id = result.getInt("id"),
                    login = result.getString("login"),
                    passwordHash = result.getString("password_hash"),
                    salt = result.getString("salt")
                )
            } else {
                null
            }
        }
    }

    override fun getAll(): List<User> {
        val users = mutableListOf<User>()
        val sql = "SELECT * FROM users"

        _connection.createStatement().use { statement ->
            val result = statement.executeQuery(sql)
            while (result.next()) {
                val user = User(
                    id = result.getInt("id"),
                    login = result.getString("login"),
                    passwordHash = result.getString("password_hash"),
                    salt = result.getString("salt")
                )
                users.add(user)
            }
        }
        return users
    }

    override fun add(user: User): Boolean {
        val sql = "INSERT INTO users (login, password_hash, salt) VALUES (?, ?, ?)"

        _connection.prepareStatement(sql).use { statement ->
            statement.setString(1, user.login)
            statement.setString(2, user.passwordHash)
            statement.setString(3, user.salt)

            return statement.executeUpdate() > 0
        }
    }

    override fun update(user: User): Boolean {
        val sql = "UPDATE users SET login = ?, password_hash = ?, salt = ? WHERE id = ?"

        _connection.prepareStatement(sql).use { statement ->
            statement.setString(1, user.login)
            statement.setString(2, user.passwordHash)
            statement.setString(3, user.salt)
            statement.setInt(4, user.id!!)

            return statement.executeUpdate() > 0
        }
    }

    override fun delete(id: Int): Boolean {
        val sql = "DELETE FROM users WHERE id = ?"

        _connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, id)
            return statement.executeUpdate() > 0
        }
    }

    override fun getByLogin(login: String): User? {
        val sql = "SELECT * FROM users WHERE login = ?"

        _connection.prepareStatement(sql).use { statement ->
            statement.setString(1, login)
            val result = statement.executeQuery()

            return if (result.next()) {
                User(
                    id = result.getInt("id"),
                    login = result.getString("login"),
                    passwordHash = result.getString("password_hash"),
                    salt = result.getString("salt")
                )
            } else {
                null
            }
        }
    }

}
