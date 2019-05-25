package ru.polyhack

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update


@Component
class PollBot(
        @Value("\${username}") val username: String,
        @Value("\${token}") val token: String,
        val userService: UserService
) : TelegramLongPollingBot() {


    override fun getBotUsername() = username

    override fun getBotToken() = token

    override fun onUpdateReceived(update: Update) {
        if (!update.hasMessage()) {
            return
        }

        val user = userService.getOrCreateUser(update.message.from)
        var message = update.message
        var text = message.text

        val response = SendMessage()
                .setChatId(update.message.chatId)
                .setText(
                        if (text == "/start" || text == "/help") {
                            """
                                 Привет!
                                 С помощью этого бота ты можешь проголосовать за проекты хакатона.
                            """.trimIndent()
                        } else {
                            text
                        }
                )

        execute(response)
    }

}