package org.example.AccessControl.data.sqlite_repos

import data.interfaces.IResourcesRepo
import org.example.AccessControl.entities.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

@Repository
class SqliteResourceRepo(@Value("\${app.db.path}") dbFilePath: String = "")
    : SqliteRepoBase(dbFilePath), IResourcesRepo {

    override fun getById(id: Int): Resource? {
        val sql = "SELECT * FROM resources WHERE id = ?"

        _connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, id)
            val result = statement.executeQuery()

            return if (result.next()) {
                val parentId = result.getInt("parent_id")
                val parent = if (parentId != 0) getById(parentId) else null

                Resource(
                    id = result.getInt("id"),
                    path = result.getString("path"),
                    maxVolume = result.getInt("max_volume"),
                    parent = parent
                )
            } else {
                null
            }
        }
    }

    override fun getAll(): List<Resource> {
        val resourcesMap = mutableMapOf<Int, Resource>()
        val resources = mutableListOf<Resource>()

        val sql = "SELECT * FROM resources ORDER BY parent_id NULLS FIRST"

        _connection.createStatement().use { statement ->
            val result = statement.executeQuery(sql)
            while(result.next()){
                val id = result.getInt("id")
                val parentId = result.getInt("parent_id")

                val parent = if (parentId != 0) resourcesMap[parentId] else null

                val resource = Resource(
                    id = id,
                    path = result.getString("path"),
                    maxVolume = result.getInt("max_volume"),
                    parent = parent
                )

                resourcesMap[id] = resource
                resources.add(resource)
            }
        }
        return resources
    }

    override fun getByPath(path: String): Resource? {
        val sql = "SELECT * FROM resources WHERE path = ?"

        _connection.prepareStatement(sql).use { statement ->
            statement.setString(1, path)
            val result = statement.executeQuery()

            return if (result.next()) {
                val parentId = result.getInt("parent_id")
                val parent = if (parentId != 0) getById(parentId) else null

                Resource(
                    id = result.getInt("id"),
                    path = result.getString("path"),
                    maxVolume = result.getInt("max_volume"),
                    parent = parent
                )
            } else {
                null
            }
        }
    }

    override fun add(resource: Resource): Boolean {
        val sql = "INSERT INTO resources (path, max_volume, parent_id) VALUES (?, ?, ?)"

        _connection.prepareStatement(sql).use { statement ->
            statement.setString(1, resource.path)
            statement.setInt(2, resource.maxVolume)
            statement.setInt(3, resource.parent?.id ?: 0)

            return statement.executeUpdate() > 0
        }
    }

    override fun update(resource: Resource): Boolean {
        val sql = "UPDATE resources SET path = ?, max_volume = ?, parent_id = ? WHERE id = ?"

        _connection.prepareStatement(sql).use { statement ->
            statement.setString(1, resource.path)
            statement.setInt(2, resource.maxVolume)
            statement.setInt(3, resource.parent?.id ?: 0)
            statement.setInt(4, resource.id!!)

            return statement.executeUpdate() > 0
        }
    }

    override fun delete(id: Int): Boolean {
        val sql = "DELETE FROM resources WHERE id = ?"

        _connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, id)
            return statement.executeUpdate() > 0
        }
    }
}