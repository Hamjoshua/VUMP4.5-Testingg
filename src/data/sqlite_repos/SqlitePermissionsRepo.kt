package org.example.data.sqlite_repos

import org.example.data.interfaces.IPermissionsRepo
import org.example.entities.Permission
import org.example.entities.ResourceAction

class SqlitePermissionsRepo(dbFilePath: String) : SqliteRepoBase(dbFilePath), IPermissionsRepo {

    override fun getById(id: Int): Permission? {
        val sql = "SELECT * FROM permissions WHERE id = ?"

        _connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, id)
            val result = statement.executeQuery()

            return if (result.next()) {
                permissionFromResultSet(result)
            } else {
                null
            }
        }
    }

    override fun getAll(): List<Permission> {
        val permissions = mutableListOf<Permission>()
        val sql = "SELECT * FROM permissions"

        _connection.createStatement().use { statement ->
            val result = statement.executeQuery(sql)
            while (result.next()) {
                permissions.add(permissionFromResultSet(result))
            }
        }
        return permissions
    }

    override fun getByUserId(userId: Int): List<Permission> {
        val permissions = mutableListOf<Permission>()
        val sql = """
            SELECT p.* FROM permissions p
            JOIN users u ON p.user_login = u.login
            WHERE u.id = ?
        """

        _connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, userId)
            val result = statement.executeQuery()

            while (result.next()) {
                permissions.add(permissionFromResultSet(result))
            }
        }
        return permissions
    }

    override fun add(permission: Permission): Boolean {
        val sql = """
            INSERT INTO permissions (user_login, resource_path, can_read, can_write, can_execute) 
            VALUES (?, ?, ?, ?, ?)
        """

        _connection.prepareStatement(sql).use { statement ->
            statement.setString(1, permission.userLogin)
            statement.setString(2, permission.resourcePath)
            statement.setInt(3, if (permission.actions.contains(ResourceAction.READ)) 1 else 0)
            statement.setInt(4, if (permission.actions.contains(ResourceAction.WRITE)) 1 else 0)
            statement.setInt(5, if (permission.actions.contains(ResourceAction.EXECUTE)) 1 else 0)

            return statement.executeUpdate() > 0
        }
    }

    override fun update(permission: Permission): Boolean {
        val sql = """
            UPDATE permissions 
            SET user_login = ?, resource_path = ?, can_read = ?, can_write = ?, can_execute = ?
            WHERE id = ?
        """

        _connection.prepareStatement(sql).use { statement ->
            statement.setString(1, permission.userLogin)
            statement.setString(2, permission.resourcePath)
            statement.setInt(3, if (permission.actions.contains(ResourceAction.READ)) 1 else 0)
            statement.setInt(4, if (permission.actions.contains(ResourceAction.WRITE)) 1 else 0)
            statement.setInt(5, if (permission.actions.contains(ResourceAction.EXECUTE)) 1 else 0)
            statement.setInt(6, permission.id!!)

            return statement.executeUpdate() > 0
        }
    }

    override fun delete(id: Int): Boolean {
        val sql = "DELETE FROM permissions WHERE id = ?"

        _connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, id)
            return statement.executeUpdate() > 0
        }
    }

    // Для Марка - крч, в чем рофл - в бд я храню разрешения в виде набора 1 и 0. Но у нас то это классный, четкий
    // Красивый enum. Поэтому так вот
    private fun permissionFromResultSet(result: java.sql.ResultSet): Permission {
        val actions = mutableSetOf<ResourceAction>()
        if (result.getInt("can_read") == 1) actions.add(ResourceAction.READ)
        if (result.getInt("can_write") == 1) actions.add(ResourceAction.WRITE)
        if (result.getInt("can_execute") == 1) actions.add(ResourceAction.EXECUTE)

        return Permission(
            id = result.getInt("id"),
            userLogin = result.getString("user_login"),
            resourcePath = result.getString("resource_path"),
            actions = actions
        )
    }
}