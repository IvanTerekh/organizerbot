package com.intetics.organizerbot;

import com.intetics.organizerbot.context.ContextHolder;
import com.intetics.organizerbot.context.Context;
import com.intetics.organizerbot.keyboards.Keyboards;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrganizerBot extends TelegramLongPollingBot {

    private static final String LOGTAG = "BOT";

    private ResourceBundle botInfo = ResourceBundle.getBundle("botinfo");
    private ResourceBundle buttons = ResourceBundle.getBundle("buttons");
    private ResourceBundle messages = ResourceBundle.getBundle("messages");
    private static ResourceBundle days = ResourceBundle.getBundle("days");

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
            case ADD_CLASS_CHOOSE_DAY:
                handleMessageFromAddClassChooseDay(message);
                break;
            case ADD_CLASS_CHOOSE_TIME:
                handleMessageFromAddClassChooseTime(message);
                break;
            case ADD_CLASS_CHOOSE_SUBJECT:
                handleMessageFromAddClassChooseSubject(message);
                break;
//            case ADD_EVENT
        }
    }

    private void handleMessageFromAddClassChooseSubject(Message message) {
        String text = message.getText();
        if (getSubjects(message.getChatId()).contains(text)){
            Object o = ContextHolder.getInstance().getEditingValue(message.getChatId());//TODO: rewrite
            ContextHolder.getInstance().removeEditingValue(message.getChatId());
            sendResponseOnChooseSubject(message);
            sendMainMenu(message);
            setContext(message.getChatId(), Context.MAIN_MENU);
        } else if(buttons.getString("mainMenu").equals(text)) {
            setContext(message.getChatId(), Context.MAIN_MENU);
            sendMainMenu(message);
        } else {
            String replyText = messages.getString("cannotUnderstand") + ' ' + messages.getString("chooseSubject");
            reply(message, replyText);
        }
    }

    private void sendResponseOnChooseSubject(Message inMessage) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(messages.getString("classAdded"));
        send(outMessage);
    }

    private void handleMessageFromAddClassChooseTime(Message message) {
        String text = message.getText();
        if (text.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")){
            Object o = ContextHolder.getInstance().getEditingValue(message.getChatId());
            ContextHolder.getInstance().setEditingValue(message.getChatId(), o);//TODO: rewrite
            sendResponseOnChooseTime(message);
            setContext(message.getChatId(), Context.ADD_CLASS_CHOOSE_SUBJECT);
        } else if(buttons.getString("mainMenu").equals(text)) {
            setContext(message.getChatId(), Context.MAIN_MENU);
            sendMainMenu(message);
        } else {
            String replyText = messages.getString("cannotUnderstand") + ' ' + messages.getString("chooseTime");
            reply(message, replyText);
        }
    }

    private void sendResponseOnChooseTime(Message inMessage) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(messages.getString("chooseSubject"));
        outMessage.setReplyMarkup(Keyboards.getSubjectListKeyboard(getSubjects(inMessage.getChatId())));
        send(outMessage);
    }

    private List<String> getSubjects(Long id) {//TODO: rewrite
        return new ArrayList<String>(){{
            add("Math");
            add("Physics");
        }};
    }

    private void handleMessageFromAddClassChooseDay(Message message) {
        String text = message.getText();
        if (days.getString("monday").equals(text) ||
                days.getString("tuesday").equals(text) ||
                days.getString("wednesday").equals(text) ||
                days.getString("thursday").equals(text) ||
                days.getString("friday").equals(text) ||
                days.getString("saturday").equals(text) ||
                days.getString("sunday").equals(text)){
            ContextHolder.getInstance().setEditingValue(message.getChatId(), null);//TODO: rewrite
            sendResponseOnChooseDay(message);
            setContext(message.getChatId(), Context.ADD_CLASS_CHOOSE_TIME);
        } else if(buttons.getString("mainMenu").equals(text)) {
            setContext(message.getChatId(), Context.MAIN_MENU);
            sendMainMenu(message);
        } else {
            String replyText = messages.getString("cannotUnderstand") + ' ' + messages.getString("chooseDay");
            reply(message, replyText);
        }
    }

    private void sendResponseOnChooseDay(Message inMessage) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(messages.getString("chooseTime"));
        outMessage.setReplyMarkup(Keyboards.getReturnToMenuKeyboard());
        send(outMessage);
    }

    private void handleMessageFromRemoveSubject(Message message) {
        if (buttons.getString("back").equals(message.getText())) {
            sendMainMenu(message);
            setContext(message.getChatId(), Context.MAIN_MENU);
        } else {
            removeSubject(message);
        }
    }

    private void removeSubject(Message inMessage) {
        //TODO: write this method
    }

    private void handleMessageFromAddSubject(Message message) {
        if (buttons.getString("back").equals(message.getText())) {
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
        if (buttons.getString("addEvent").equals(inMessage.getText())) {
            sendResponseOnAddEvent(inMessage);
            setContext(inMessage.getChatId(), Context.ADD_EVENT_CHOOSE_DATE);
        } else if (buttons.getString("addClass").equals(inMessage.getText())) {
            sendResponseOnAddClass(inMessage);
            setContext(inMessage.getChatId(), Context.ADD_CLASS_CHOOSE_DAY);
        } else if (buttons.getString("addSubject").equals(inMessage.getText())) {
            sendResponseOnAddSubject(inMessage);
            setContext(inMessage.getChatId(), Context.ADD_SUBJECT);
        } else if (buttons.getString("removeSubject").equals(inMessage.getText())) {
            sendResponseOnRemoveSubject(inMessage);
            setContext(inMessage.getChatId(), Context.REMOVE_SUBJECT);
        } else if ("/start".equals(inMessage.getText())) {
            sendMainMenu(inMessage);
        } else {
            sendMainMenu(inMessage, messages.getString("cannotUnderstand") + messages.getString("chooseFromMenu"));
        }
    }

    private void sendResponseOnAddEvent(Message inMessage) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(messages.getString("chooseDate"));
        outMessage.setReplyMarkup(Keyboards.getCalendarKeyboard());
        send(outMessage);
    }

    private void sendResponseOnAddClass(Message inMessage) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(messages.getString("chooseDay"));
        outMessage.setReplyMarkup(Keyboards.getDaysListKeyboard());
        send(outMessage);
    }

    private void sendResponseOnRemoveSubject(Message inMessage) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(messages.getString("noSubjects"));
        outMessage.setReplyMarkup(Keyboards.getAddSubjectKeyboard());//TODO: rewrite this method
        //outMessage.setReplyMarkup(Keyboards.getSubjectListKeyboard(subjects));
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

    private void reply(Message inMessage, String text) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(text);
        send(outMessage);
    }

}