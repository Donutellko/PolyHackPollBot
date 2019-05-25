package ru.polyhack

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update


@Component
class PollBot : TelegramLongPollingBot() {


    @Value("\${username}")
    val username: String? = null

    @Value("\${token}")
    val token: String? = null


    override fun getBotUsername() = username

    override fun getBotToken() = token

    override fun onUpdateReceived(update: Update) {
        update.updateId
        if (!update.hasMessage()) return

        val message = SendMessage()
                .setChatId(update.message.chatId)
                .setText(update.message.text)

        execute(message)
    }

}