package ru.polyhack;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@SpringBootApplication
public class Application implements CommandLineRunner {

static {
    ApiContextInitializer.init();
}

@Autowired
private PollBot pollBot;

public static void main(String[] args) {
    try {
        SpringApplication.run(Application.class, args);
    } catch (BeanCreationException e) {
        if (e.toString().contains("Error removing old webhook")) {
            System.out.println("РКН разбушевался, нужен прокси или VPN.");
        }
    }

}

@Override
public void run(String... args) throws Exception {
    TelegramBotsApi botsApi = new TelegramBotsApi();
    botsApi.registerBot(pollBot);
}

}
