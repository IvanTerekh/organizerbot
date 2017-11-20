package com.intetics.organizerbot;

import com.intetics.organizerbot.context.ContextHolder;
import com.intetics.organizerbot.context.Context;
import com.intetics.organizerbot.keyboards.Keyboards;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.ResourceBundle;

public class OrganizerBot  extends TelegramLongPollingBot {

    private static final String LOGTAG = "BOT";

    private ResourceBundle botInfo = ResourceBundle.getBundle("botinfo");

    public String getBotUsername() {
        return botInfo.getString("username");
    }

    @Override
    public String getBotToken() {
        return botInfo.getString("token");
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        }
    }

    private void handleMessage(Message message) {
        Long chatId = message.getChatId();
        if(!ContextHolder.getInstance().contains(message.getChatId())){
            ContextHolder.getInstance().setContext(chatId, Context.MAIN_MENU);
            sendGreetingMessage(message);
        } else {
            Context chatContext = ContextHolder.getInstance().getContext(chatId);
            handleMessageInContext(message, chatContext);
        }
    }

    private void sendGreetingMessage(Message inMessage) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText("Hello, " + inMessage.getChat().getFirstName());
        outMessage.setReplyMarkup(Keyboards.getMainMenuKeyboard());
        send(outMessage);
    }

    private void send(SendMessage outMessage) {
        try {
            execute(outMessage);
        } catch (TelegramApiException e) {
            BotLogger.severe(LOGTAG, e);
        }
    }

    private void handleMessageInContext(Message message, Context context) {
        switch (context){
            case MAIN_MENU:
                handleMessageFromMainMenu(message);
                break;
            case ADD_SUBJECT:
                break;
        }
    }

    private void handleMessageFromMainMenu(Message inMessage) {

    }

}