package ru.polyhack

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.User as TgUser

@Service
class VoteService(
        @Value("\${votes-limit}") val votesLimit: Long,
        private val userRep: UserRep
        ) {

    fun addVote(user: User, project: Project) {
        if (user.votes.size > votesLimit) throw Exception("Вы уже проголосовали за $votesLimit проектов.")
    }

}

@Service
class UserService(private val userRep: UserRep) {

    fun getOrCreateUser(tgUser: TgUser): User {
        val u = userRep.findById(tgUser.id)
        if (u.isPresent) return u.get()

        return userRep.save(User(tgUser))
    }

}

@Service
class LogService(private val logRep: LogRep) {

    fun addLog(tag: String, text: String, response: String) {
        logRep.save(Log(tag = tag, text = text, response = response))
    }

}