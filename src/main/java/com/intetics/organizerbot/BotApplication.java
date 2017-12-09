package com.intetics.organizerbot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;

import java.io.IOException;
import java.util.Locale;

public class BotApplication {
    private static final String LOGTAG = "APP";

    public static void main(String[] args) {
        regiterLogger("bot.log");
        Locale.setDefault(Locale.UK);
        registerBot();
    }

    private static void registerBot() {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new OrganizerBot());
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    private static void regiterLogger(String logFileName) {
        try {
            BotLogger.registerLogger(new BotsFileHandler(logFileName));
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        }
    }

}