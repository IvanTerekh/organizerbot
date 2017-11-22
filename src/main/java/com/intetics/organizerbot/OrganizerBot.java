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

public class OrganizerBot extends TelegramLongPollingBot {

    private static final String LOGTAG = "BOT";

    private ResourceBundle botInfo = ResourceBundle.getBundle("botinfo");
    private ResourceBundle buttons = ResourceBundle.getBundle("buttons");
    private ResourceBundle messages = ResourceBundle.getBundle("messages");

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
        Long userId = message.getChatId();
        if (!ContextHolder.getInstance().contains(message.getChatId())) {
            setContext(userId, Context.MAIN_MENU);
        }
        Context chatContext = ContextHolder.getInstance().getContext(userId);
        handleMessageInContext(message, chatContext);
    }

    private void sendMainMenu(Message inMessage) {
        sendMainMenu(inMessage, messages.getString("chooseOption"));
    }

    private void sendMainMenu(Message inMessage, String outMessageText) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(outMessageText);
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
        switch (context) {
            case MAIN_MENU:
                handleMessageFromMainMenu(message);
                break;
            case ADD_SUBJECT:
                handleMessageFromAddSubject(message);
                break;
            case REMOVE_SUBJECT:
                handleMessageFromRemoveSubject(message);
                break;
        }
    }

    private void handleMessageFromRemoveSubject(Message message) {
        if(buttons.getString("back").equals(message.getText())) {
            sendMainMenu(message);
            setContext(message.getChatId(), Context.MAIN_MENU);
        } else {
            removeSubject(message);
        }
    }

    private void removeSubject(Message inMessage) {
    }

    private void handleMessageFromAddSubject(Message message) {
        if(buttons.getString("back").equals(message.getText())) {
            sendMainMenu(message);
            setContext(message.getChatId(), Context.MAIN_MENU);
        } else {
            addSubject(message);
            setContext(message.getChatId(), Context.MAIN_MENU);
        }
    }

    private void addSubject(Message inMessage) {
        //TODO: rewrite this method
        sendMainMenu(inMessage, messages.getString("subjectAdded"));
    }

    private void handleMessageFromMainMenu(Message inMessage) {
        if ("/start".equals(inMessage.getText())){
            sendMainMenu(inMessage);
        } else if(buttons.getString("addSubject").equals(inMessage.getText())) {
            sendResponseOnAddSubject(inMessage);
            setContext(inMessage.getChatId(), Context.ADD_SUBJECT);
        } else if(buttons.getString("removeSubject").equals(inMessage.getText())) {
            sendResponseOnRemoveSubject(inMessage);
            setContext(inMessage.getChatId(), Context.REMOVE_SUBJECT);
        } else {
            sendMainMenu(inMessage, messages.getString("cannotUnderstand") + messages.getString("chooseFromMenu"));
        }
    }

    private void sendResponseOnRemoveSubject(Message inMessage) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(messages.getString("noSubjects"));
        outMessage.setReplyMarkup(Keyboards.getAddSubjectKeyboard());//TODO: rewrite this method
        //outMessage.setReplyMarkup(Keyboards.getRemoveSubjectKeyboard(subjects));
        send(outMessage);
    }

    private void sendResponseOnAddSubject(Message inMessage) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(messages.getString("typeSubject"));
        outMessage.setReplyMarkup(Keyboards.getAddSubjectKeyboard());
        send(outMessage);
    }

    private void setContext(Long userId, Context addSubject) {
        ContextHolder.getInstance().setContext(userId, addSubject);
    }

}