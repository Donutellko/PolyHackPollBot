package ru.polyhack

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRep : JpaRepository<User, String> {

    fun findByUsername(name: String): Optional<User>
}

@Repository
interface ProjectRep : JpaRepository<Project, Int> {

    @Query("""
        select p, count(*) as cnt
        from User u join u.votes p
        group by p.id
        order by cnt desc
    """)
    fun getStats(): Collection<Array<Any>>

}

@Repository
interface LogRep : JpaRepository<Log, Long>