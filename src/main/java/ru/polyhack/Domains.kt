package ru.polyhack

import java.time.LocalDate
import javax.persistence.*

@Entity
class User(
        @Id var id: Long? = null,
        @OneToMany var votes: MutableSet<Project> = mutableSetOf()
)


@Entity
class Project(var name: String? = null) {

    @Id
    @GeneratedValue
    var id: Long? = null

}


@Entity
class Log(var user: String? = null, var text: String? = null) {

    @Id
    @GeneratedValue
    var id: Long? = null

    var timestamp: LocalDate? = LocalDate.now()

}