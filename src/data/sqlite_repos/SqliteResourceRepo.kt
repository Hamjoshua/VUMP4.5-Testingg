package org.example.data.sqlite_repos

import org.example.data.interfaces.IResourcesRepo
import org.example.entities.Resource

class SqliteResourceRepo(dbFilePath: String) : SqliteRepoBase(dbFilePath), IResourcesRepo  {
    override fun getById(id: Int): Resource? {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Resource> {
        val resources : MutableList<Resource> = mutableListOf()

        val sql = "select * from Resource"

        _connection.createStatement().use {
            val result = it.executeQuery(sql)
            while(result.next()){
                val parentId = result.getInt("parentId")
                var parentResource : Resource? = null
                if(parentId != 0){
                    parentResource = resources[parentId - 1]
                }

                val dbRes = Resource(
                    path = result.getString("path"),
                    maxVolume = result.getInt("maxVolume"),
                    parent = parentResource
                )

                resources.add(dbRes)
            }
        }

        return resources.toList()
    }

    override fun getByPath(path: String): Resource? {
        TODO("Not yet implemented")
    }

    override fun add(resource: Resource): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(resource: Resource): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

}
