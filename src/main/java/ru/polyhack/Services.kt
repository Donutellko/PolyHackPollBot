package ru.polyhack

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class VoteService(
        @Value("\${votes-limit}") val votesLimit: Long,
        private val userRep: UserRep
        ) {

    fun addVote(user: User, project: Project) {
        if (user.votes.size > votesLimit) throw Exception("Вы уже проголосовали за $votesLimit проектов.")
    }

}