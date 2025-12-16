package data.jpa_repos

import org.example.AccessControl.entities.Resource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ResourceRepository : JpaRepository<Resource, Int> {
    fun findByPath(path: String): Resource?

    @Query("SELECT r FROM Resource r WHERE r.parent.id = :parentId")
    fun findAllByParentId(parentId: Int): List<Resource>

    fun existsByPath(path: String): Boolean
}