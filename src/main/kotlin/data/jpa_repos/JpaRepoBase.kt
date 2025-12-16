package data.jpa_repos

import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
open class JpaRepoBase(
    private val dataSource: DataSource
) {
    protected val connection by lazy { dataSource.connection }
}