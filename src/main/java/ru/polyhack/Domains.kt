@file:Suppress("unused")

package ru.polyhack

import java.time.LocalDateTime
import javax.persistence.*
import org.telegram.telegrambots.meta.api.objects.User as TgUser

@Entity
class User(
        var chatId: Int? = null,
        @Id var username: String? = null,
        var firstname: String? = null,
        var lastname: String? = null,
        @ManyToMany(fetch = FetchType.EAGER) var votes: MutableSet<Project> = mutableSetOf()
) {
    constructor(tgUser: TgUser) : this(
            chatId = tgUser.id,
            username = tgUser.userName,
            firstname = tgUser.firstName,
            lastname = tgUser.lastName
    )
}


@Entity
class Project(
        @Id var id: Int? = null,
        var name: String? = null,
        var team: String? = null,
        var members: String? = null // участники
)


@Entity
class Log(
        @Column(length = 4000) var tag: String? = null,
        @Column(length = 4000) var text: String? = null,
        @Column(length = 4000) var response: String? = null
) {

    @Id
    @GeneratedValue
    var id: Long? = null

    var timestamp: LocalDateTime? = LocalDateTime.now()

}