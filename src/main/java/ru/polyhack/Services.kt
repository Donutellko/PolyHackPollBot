package ru.polyhack

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.stream.Collectors
import org.telegram.telegrambots.meta.api.objects.User as TgUser

@Service
class UserService(
        @Value("\${votes-limit}") val votesLimit: Long,
        private val userRep: UserRep
) {

    fun getOrCreateUser(tgUser: TgUser): User {
        val u = userRep.findById(tgUser.id)
        if (u.isPresent) return u.get()

        return userRep.save(User(tgUser))
    }

    fun vote(user: User, project: Project): Boolean {
        if (user.votes.size >= votesLimit) throw Exception("Вы уже проголосовали за $votesLimit проектов.")

        if (user.votes.any { p -> p.id == project.id }) throw Exception("Вы уже голосовали за этот проект.")

        user.votes.add(project)
        userRep.save(user)
        return true
    }

    fun revoke(user: User) {
        user.votes.clear()
        userRep.save(user)
    }

    fun getTextVotes(user: User): String =
            user.votes.stream()
                    .map { p -> "${p.id}:\t${p.name} ${if (p.team.isNullOrBlank()) "" else "(${p.team})"}" }
                    .collect(Collectors.joining("\n"))


}

@Service
class LogService(private val logRep: LogRep) {

    fun addLog(tag: String, text: String, response: String) {
        logRep.save(Log(tag = tag, text = text, response = response))
    }

}

@Service
class ProjectService(
        private val projectRep: ProjectRep,
        private val userRep: UserRep
) {


    fun findById(id: Int): Project =
            projectRep.findById(id).orElse(null)
                    ?: throw Exception("Проект с таким id не найден")


    fun getTextList(): String =
            projectRep.findAll().stream()
                    .map { p -> "${p.id}:\t${p.name} ${if (p.team.isNullOrBlank()) "" else "(${p.team})"}" }
                    .collect(Collectors.joining("\n"))


    fun add(name: String, team: String) =
            projectRep.save(Project(name = name, team = team))


    fun getTextStats(): String =
            projectRep.getStats().stream()
                    .map { s -> "${s[1]} голос${ending((s[1] as Long).toInt())}: ${(s[0] as Project).name}" }
                    .collect(Collectors.joining("\n"))

    private fun ending(n: Int) = when {
        n % 100 in 15..19 -> "ов"
        n % 10 == 1 -> ""
        else -> "а"
    }


    /** Удалить все голоса и обновить список проектов
     * Формат строк: название проекта - команда */
    fun reloadAll(str: String) {

        userRep.deleteAll()
        projectRep.deleteAll()

        val list = str.split("\n").filter { s -> s.contains(" - ") }

        for (i in 1..list.size) {
            val tmp = list[i - 1].split(" - ")
            projectRep.save(Project(id = i, name = tmp[0], team = tmp[1]))
        }
    }
}