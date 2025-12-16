package data.jpa_repos

import org.example.AccessControl.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Int> {
    fun findByLogin(login: String): User?
    fun existsByLogin(login: String): Boolean
}