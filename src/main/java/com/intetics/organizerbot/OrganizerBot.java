package com.intetics.organizerbot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.Properties;
import java.util.ResourceBundle;

public class OrganizerBot  extends TelegramLongPollingBot {

    private static final String LOGTAG = "BOT";

    private ResourceBundle botInfo = ResourceBundle.getBundle("botinfo");

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            HandleMessage(update.getMessage());
        }
    }

    private void HandleMessage(Message inMessage) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(inMessage.getText());
        try {
            execute(outMessage);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    public String getBotUsername() {
        return botInfo.getString("username");
    }

    @Override
    public String getBotToken() {
        return botInfo.getString("token");
    }
}