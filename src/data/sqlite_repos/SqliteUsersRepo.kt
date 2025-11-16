package org.example.data.sqlite_repos

import org.example.data.interfaces.IUsersRepo
import org.example.entities.User

class SqliteUsersRepo(dbFilePath: String) : SqliteRepoBase(dbFilePath), IUsersRepo {
    override fun getById(id: Int): User? {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<User> {
        TODO("Not yet implemented")
    }

    override fun add(user: User): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(user: User): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

}
