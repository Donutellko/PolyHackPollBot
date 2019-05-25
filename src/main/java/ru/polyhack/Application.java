package ru.polyhack;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class Application implements CommandLineRunner {

static {
    ApiContextInitializer.init();
}

public static void main(String[] args) {
//    System.setProperty("http.proxyHost", "157.230.182.46");
//    System.setProperty("http.proxyPort", "8080");
//
//    System.setProperty("https.proxyHost", "54.39.209.44");
//    System.setProperty("https.proxyPort", "3128");

//    System.out.println("my IP (http): " + new RestTemplate().getForEntity("http://ipinfo.io/ip", String.class));
//    System.out.println("my IP (https): " + new RestTemplate().getForEntity("https://ipinfo.io/ip", String.class));
    System.out.println(("ping telegram: " + new RestTemplate().getForEntity("https://api.telegram.org", String.class)).substring(0, 100));

    try {
        SpringApplication.run(Application.class, args);
    } catch (BeanCreationException e) {
        if (e.toString().contains("Error removing old webhook")) {
            System.out.println("РКН разбушевался, нужен прокси или VPN.");
        }
    }

}

@Override
public void run(String... args) {

}

}
