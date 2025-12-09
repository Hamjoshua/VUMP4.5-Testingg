package data.interfaces

import org.example.AccessControl.entities.Resource

interface IResourcesRepo {
    fun getById(id: Int): Resource?
    fun getAll(): List<Resource>
    fun getByPath(path: String): Resource?
    fun add(resource: Resource): Boolean
    fun update(resource: Resource): Boolean
    fun delete(id: Int): Boolean
}