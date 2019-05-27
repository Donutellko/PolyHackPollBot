package ru.polyhack

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.ApiContext
import org.telegram.telegrambots.meta.api.methods.send.*
import org.telegram.telegrambots.meta.api.objects.Update
import java.lang.NumberFormatException

@Component
class PollBot(
        @Value("\${username}") val username: String,
        @Value("\${token}") val token: String,
        @Value("\${votes-limit}") val votesLimit: Long,
        private val userService: UserService,
        private val logService: LogService,
        private val projectServ: ProjectService
) : TelegramLongPollingBot(
        ApiContext.getInstance(DefaultBotOptions::class.java).apply {
            proxyHost = "54.39.209.44"
            proxyPort = 3128
            proxyType = DefaultBotOptions.ProxyType.HTTP
        }
) {

    enum class Stickers { COOL, ANGRY, WHAT, LOVE, CAKE }

    private val stickers = mapOf(
            Stickers.COOL to "CAADAgADCQADkBSzIXZlSs_QyYc9Ag",
            Stickers.ANGRY to "CAADAgADBwADkBSzIamaMEDT6-0rAg",
            Stickers.WHAT to "CAADAgADCgADkBSzIZKTLtxihVueAg",
            Stickers.LOVE to "CAADAgADBgADkBSzIQHQ53WkOU0dAg",
            Stickers.CAKE to "CAADAgADCAADkBSzIXCJL2ZC7QW6Ag"
    )

    private val helpText = """
        Привет! С помощью этого бота ты можешь проголосовать за проекты хакатона.
        Всего ты можешь проголосовать за $votesLimit проектов.

        Вот, что ты можешь сделать:
        /help - увидеть это сообщение
        /list - получить список проектов с их идентификаторами (id)
        /vote [id] - проголосовать (без квадратных скобок)
        /votes - посмотреть свои голоса
        /revoke - удалить свои голоса
        /stats - увидеть текущие результаты
    """.trimIndent()

    override fun getBotUsername() = username

    override fun getBotToken() = token

    override fun onUpdateReceived(update: Update) {
        if (!update.hasMessage()) {
            return
        }

        if (update.message.text.contains("/pass НИПУТЮ")) {
            userService.addUser(update.message.from)

            logService.addLog(tag = update.message.from.userName ?: "unknown",
                    text = update.message.text,
                    response = "Добро пожаловать")

            execute(SendPhoto()
                    .setChatId(update.message.chatId)
                    .setPhoto("https://i.kym-cdn.com/news_feeds/icons/mobile/000/030/918/0c2.jpg")
            )

            return
        }

        val user = try {
            userService.getExistingUser(update.message.from)
        } catch (e: Exception) {
            logService.addLog(tag = update.message.from.userName ?: "unknown",
                    text = update.message.text,
                    response = e.message ?: "Произошла ошибка при поиске пользователя в базе.")

            execute(SendMessage()
                    .setChatId(update.message.chatId)
                    .setText(e.message ?: "Произошла ошибка при поиске пользователя в базе.")
            )

            execute(SendSticker().apply {
                setChatId(update.message.chatId)
                setSticker(stickers[Stickers.ANGRY])
            })

            return
        }

        val requestText = update.message.text ?: ""

        var responseSticker: Stickers? = null

        val responseText: String = if (requestText.contains("/start") || requestText.contains("/help")) {

            helpText

        } else if (requestText.contains("/list")) {

            val list = projectServ.getTextList()
            if (list.isBlank())
                "В БД нет ни одного проекта..."
            else
                "Список зарегистрированных команд: \n$list"

        } else if (requestText.contains("/revoke")) {

            userService.revoke(user)
            "Голоса удалены."

        } else if (requestText.contains("/votes")) {

            val votes = userService.getTextVotes(user)
            if (votes.isBlank())
                "Вы ни за кого не голосовали."
            else {
                responseSticker = Stickers.LOVE
                "Вы проголосовали за:\n$votes"
            }

        } else if (requestText.contains("/vote")) {

            try {

                val id = requestText.replace("/vote", "").trim()
                val project = projectServ.findById(id.toInt())
                userService.vote(user, project)
                "Спасибо! Ваш голос за \"${project.name}\" учтён."

            } catch (e: NumberFormatException) {

                "Нужно указать номер проекта, например: /vote 42"

            } catch (e: Exception) {

                responseSticker = Stickers.ANGRY
                e.printStackTrace()

                e.message ?: "Произошла ошибка."

            }

        } else if (requestText.contains("/stats")) {

            val stats = projectServ.getTextStats()
            if (stats.isBlank())
                "Нет ни одного голоса"
            else {
                responseSticker = Stickers.CAKE
                "Количество голосов за команды: \n$stats"
            }

        } else if (requestText.contains(Regex("/reloadall1488\\s+"))) {

            try {
                projectServ.reloadAll(requestText)
                "Обновлено"
            } catch (e: Exception) {
                e.printStackTrace()
                e.message ?: "Ошибка!"
            }

        } else {

            responseSticker = Stickers.WHAT
            "Извини, я не понял. Введи /help, чтобы увидеть доступные команды."

        }

        logService.addLog(tag = user.username ?: "unknown",
                text = if (update.message.hasText()) requestText else if (update.message.hasSticker()) "sticker" else update.message.toString(),
                response = "$responseText $responseSticker")

        if (responseText != null) {
            execute(SendMessage()
                    .setChatId(update.message.chatId)
                    .setText(responseText)
            )
        }

        if (responseSticker != null) {
            execute(SendSticker().apply {
                setChatId(user.chatId!!.toLong())
                setSticker(stickers[responseSticker])
            })
        }
    }

}