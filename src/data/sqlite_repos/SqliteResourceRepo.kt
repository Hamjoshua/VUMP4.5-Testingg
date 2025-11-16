package org.example.data.sqlite_repos

import org.example.data.interfaces.IResourcesRepo
import org.example.entities.Resource

class SqliteResourceRepo(dbFilePath: String) : SqliteRepoBase(dbFilePath), IResourcesRepo  {
    override fun getById(id: Int): Resource? {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Resource> {
        TODO("Not yet implemented")
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
