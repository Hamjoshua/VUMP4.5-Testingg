package org.example.data.interfaces
import org.example.entities.User

interface IUsersRepo {
    fun getById(id: Int): User?
    fun getByLogin(login: String): User?
    fun getAll(): List<User>
    fun add(user: User): Boolean
    fun update(user: User): Boolean
    fun delete(id: Int): Boolean
}