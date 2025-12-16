package data.jpa_repos

import data.interfaces.IUsersRepo
import org.example.AccessControl.entities.User
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class JpaUsersRepo(
    private val userRepository: UserRepository
) : IUsersRepo {

    override fun getById(id: Int): User? = userRepository.findById(id).orElse(null)

    override fun getByLogin(login: String): User? = userRepository.findByLogin(login)

    override fun getAll(): List<User> = userRepository.findAll()

    @Transactional
    override fun add(user: User): Boolean {
        return try {
            userRepository.save(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    @Transactional
    override fun update(user: User): Boolean {
        return if (user.id != null && userRepository.existsById(user.id)) {
            userRepository.save(user)
            true
        } else {
            false
        }
    }

    @Transactional
    override fun delete(id: Int): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    override fun existsByLogin(login: String): Boolean = userRepository.existsByLogin(login)
}