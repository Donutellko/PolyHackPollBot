package ru.polyhack

import java.time.LocalDate
import javax.persistence.*
import org.telegram.telegrambots.meta.api.objects.User as TgUser

@Entity
class User(
        @Id var id: Int? = null,
        var username: String? = null,
        var firstname: String? = null,
        var lastname: String? = null,
        @OneToMany var votes: MutableSet<Project> = mutableSetOf()
) {
    constructor(tgUser: TgUser) : this(
            id = tgUser.id,
            username = tgUser.userName,
            firstname = tgUser.userName,
            lastname = tgUser.lastName
    )
}


@Entity
class Project(var name: String? = null) {

    @Id
    @GeneratedValue
    var id: Long? = null

}


@Entity
class Log(var tag: String? = null, var text: String? = null, var response: String? = null) {

    @Id
    @GeneratedValue
    var id: Long? = null

    var timestamp: LocalDate? = LocalDate.now()

}