package org.example.data.sqlite_repos

import org.example.data.interfaces.IPermissionsRepo
import org.example.entities.Permission

class SqlitePermissionsRepo(dbFilePath: String) : SqliteRepoBase(dbFilePath), IPermissionsRepo {
    override fun getById(id: Int): Permission? {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Permission> {
        TODO("Not yet implemented")
    }

    override fun getByUserId(userId: Int): List<Permission> {
        TODO("Not yet implemented")
    }

    override fun add(permission: Permission): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(permission: Permission): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

}
