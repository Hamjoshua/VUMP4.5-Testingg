package org.example.data.interfaces
import org.example.entities.Permission

interface IPermissionsRepo {
    fun getById(id: Int): Permission?
    fun getAll(): List<Permission>
    fun getByUserId(userId: Int): List<Permission>
    fun add(permission: Permission): Boolean
    fun update(permission: Permission): Boolean
    fun delete(id: Int): Boolean
}