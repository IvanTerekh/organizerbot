package com.intetics.organizerbot;

import com.intetics.organizerbot.context.ContextHolder;
import com.intetics.organizerbot.context.Context;
import com.intetics.organizerbot.keyboards.Keyboards;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleCallbackQuery(CallbackQuery query) {
        Context context = ContextHolder.getInstance().getContext(query.getMessage().getChatId());
        switch (context) {
            case ADD_CLASS_CHOOSE_DATE:
                handleCallbackQueryFromAddClassChooseDate(query);
                break;
        }
    }

    private void handleCallbackQueryFromAddClassChooseDate(CallbackQuery query) {
        String data = query.getData();
        if (data.startsWith("goto:")) {
            resetCalendar(query);
        } else if (data.startsWith("choose:")) {//TODO: use dao
            LocalDate date = LocalDate.parse(data.split(":")[1]);
            reply(query.getMessage(), date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
            setContext(query.getMessage().getChatId(), Context.ADD_CLASS_CHOOSE_TIME);
            reply(query.getMessage(), messages.getString("chooseTime"), Keyboards.getReturnToMenuKeyboard());
        }
    }

    private void resetCalendar(CallbackQuery query) {
        EditMessageText editMarkup = new EditMessageText();
        editMarkup.setChatId(query.getMessage().getChatId().toString());
        editMarkup.setInlineMessageId(query.getInlineMessageId());
        editMarkup.enableMarkdown(true);
        editMarkup.setText(messages.getString("chooseDate2"));
        editMarkup.setMessageId(query.getMessage().getMessageId());
        editMarkup.setReplyMarkup(Keyboards.getCalendarKeyboard(YearMonth.parse(query.getData().split(":")[1])));
        try {
            execute(editMarkup);
        } catch (TelegramApiException e) {
            BotLogger.severe(LOGTAG, e);
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

    private void handleMessageInContext(Message message, Context context) {
        switch (context) {
            case MAIN_MENU:
                handleMessageFromMainMenu(message);
                break;
            case ADD_CLASS_CHOOSE_SUBJECT:
                handleMessageFromAddClassChooseSubject(message);
                break;
            case ADD_CLASS_CHOOSE_DATE_OR_DAY:
                handleMessageFromAddClassChooseDateOrDay(message);
                break;
            case ADD_CLASS_CHOOSE_DAY:
                handleMessageFromAddClassChooseDay(message);
                break;
            case ADD_CLASS_CHOOSE_DATE:
                handleMessageFromAddClassChooseDate(message);
                break;
            case ADD_CLASS_CHOOSE_TIME:
                handleMessageFromAddClassChooseTime(message);
                break;
            case ADD_CLASS_CHOOSE_TYPE:
                handleMessageFromAddClassChooseType(message);
                break;
            case ADD_CLASS_CHOOSE_ROOM:
                handleMessageFromAddClassChooseRoom(message);
                break;
//            case ADD_EVENT_CHOOSE_DATE:
//                handleMessageFromAddEventChooseDate(message);
//                break;
//            case ADD_EVENT_CHOOSE_TIME:
//                handleMessageFromAddEventChooseTime(message);
//                break;
//            case ADD_EVENT_CHOOSE_DESCRIPTION:
//                handleMessageFromAddEventChooseDescription(message);
//                break;
//            case ADD_SUBJECT:
//                handleMessageFromAddSubject(message);
//                break;
//            case REMOVE_SUBJECT:
//                handleMessageFromRemoveSubject(message);
//                break;
        }
    }

    private void handleMessageFromAddClassChooseDate(Message message) {
        String text = message.getText();
        System.out.println("message:" + text + "end");
        if (buttons.getString("mainMenu").equals(text)) {
            setContext(message.getChatId(), Context.MAIN_MENU);
            sendMainMenu(message);
        } else {
            String replyText = messages.getString("cannotUnderstand") + ' ' + messages.getString("chooseDate1");
            reply(message, replyText);
        }
    }

    private void handleMessageFromAddClassChooseRoom(Message message) {
        String text = message.getText();
        if (!buttons.getString("mainMenu").equals(text)) {
            reply(message, messages.getString("classAdded"));
        }
        setContext(message.getChatId(), Context.MAIN_MENU);
        sendMainMenu(message);
    }

    private void handleMessageFromAddClassChooseType(Message message) {
        String text = message.getText();
        if (buttons.getString("mainMenu").equals(text)) {
            setContext(message.getChatId(), Context.MAIN_MENU);
            sendMainMenu(message);
        } else {//TODO: use dao
            setContext(message.getChatId(), Context.ADD_CLASS_CHOOSE_ROOM);
            reply(message, messages.getString("choosePlace"), Keyboards.getReturnToMenuKeyboard());
        }
    }

    private void handleMessageFromAddClassChooseDateOrDay(Message message) {
        String text = message.getText();
        if (buttons.getString("mainMenu").equals(text)) {
            setContext(message.getChatId(), Context.MAIN_MENU);
            sendMainMenu(message);
        } else if (buttons.getString("oneTime").equals(text)) {
            setContext(message.getChatId(), Context.ADD_CLASS_CHOOSE_DATE);
            reply(message, messages.getString("chooseDate1"), Keyboards.getReturnToMenuKeyboard());
            reply(message, messages.getString("chooseDate2"), Keyboards.getCalendarKeyboard());
        } else if (buttons.getString("weekly").equals(text)) {
            setContext(message.getChatId(), Context.ADD_CLASS_CHOOSE_DAY);
            reply(message, messages.getString("chooseDay"), Keyboards.getDaysListKeyboard());
        } else {
            String replyText = messages.getString("cannotUnderstand") + ' ' + messages.getString("chooseFromMenu");
            reply(message, replyText);
        }
    }

//    private void handleMessageFromAddEventChooseDate(Message message) {
//        String text = message.getText();
//        if (buttons.getString("mainMenu").equals(text)) {
//            setContext(message.getChatId(), Context.MAIN_MENU);
//            sendMainMenu(message);
//        } else {
//            String replyText = messages.getString("cannotUnderstand") + ' ' + messages.getString("chooseDate1");
//            reply(message, replyText);
//        }
//    }
//
//    private void handleMessageFromAddEventChooseDescription(Message message) {
//        String text = message.getText();
//        if (buttons.getString("mainMenu").equals(text)) {
//            setContext(message.getChatId(), Context.MAIN_MENU);
//            sendMainMenu(message);
//        } else {
//            Object o = ContextHolder.getInstance().getEditingValue(message.getChatId());
//            ContextHolder.getInstance().setEditingValue(message.getChatId(), o);
//            reply(message, messages.getString("eventAdded"));
//            sendMainMenu(message);
//            setContext(message.getChatId(), Context.MAIN_MENU);
//        }
//    }
//
//    private void handleMessageFromAddEventChooseTime(Message message) {
//        String text = message.getText();
//        if (text.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
//            Object o = ContextHolder.getInstance().getEditingValue(message.getChatId());
//            ContextHolder.getInstance().setEditingValue(message.getChatId(), o);
//            sendResponseOnChooseTimeFromAddEvent(message);
//            setContext(message.getChatId(), Context.ADD_EVENT_CHOOSE_DESCRIPTION);
//        } else if (buttons.getString("mainMenu").equals(text)) {
//            setContext(message.getChatId(), Context.MAIN_MENU);
//            sendMainMenu(message);
//        } else {
//            String replyText = messages.getString("cannotUnderstand") + ' ' + messages.getString("chooseTime");
//            reply(message, replyText);
//        }
//    }
//
//    private void sendResponseOnChooseTimeFromAddEvent(Message inMessage) {
//        SendMessage outMessage = new SendMessage();
//        outMessage.setChatId(inMessage.getChatId());
//        outMessage.setText(messages.getString("chooseDescription"));
//        outMessage.setReplyMarkup(Keyboards.getReturnToMenuKeyboard());
//        send(outMessage);
//    }

    private void handleMessageFromAddClassChooseSubject(Message message) {
        String text = message.getText();
        if (buttons.getString("mainMenu").equals(text)) {
            setContext(message.getChatId(), Context.MAIN_MENU);
            sendMainMenu(message);
        } else {//TODO: use dao
            setContext(message.getChatId(), Context.ADD_CLASS_CHOOSE_DATE_OR_DAY);
            reply(message, messages.getString("chooseDateOrDay"), Keyboards.getOneTimeOrWeeklyKeyboard());
        }
    }

    private void handleMessageFromAddClassChooseTime(Message message) {
        String text = message.getText();
        if (buttons.getString("mainMenu").equals(text)) {
            setContext(message.getChatId(), Context.MAIN_MENU);
            sendMainMenu(message);
        } else if (validTime(text)) {//TODO: use dao
            setContext(message.getChatId(), Context.ADD_CLASS_CHOOSE_TYPE);
            reply(message, messages.getString("chooseType"), Keyboards.getClassTypesListKeyboard());
        } else {
            String replyText = messages.getString("cannotUnderstand") + ' ' + messages.getString("chooseTime");
            reply(message, replyText);
        }
    }

    private boolean validTime(String string){
        return string.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$");
    }

    private List<String> getSubjects(Long id) {//TODO: use dao
        return new ArrayList<String>() {{
            add("Math");
            add("Physics");
        }};
    }

    private void handleMessageFromAddClassChooseDay(Message message) {
        String text = message.getText();
        if (validDayOfWeek(text)) {
            ContextHolder.getInstance().setEditingValue(message.getChatId(), null);//TODO: use dao
            reply(message, messages.getString("chooseTime"), Keyboards.getReturnToMenuKeyboard());
            setContext(message.getChatId(), Context.ADD_CLASS_CHOOSE_TIME);
        } else if (buttons.getString("mainMenu").equals(text)) {
            setContext(message.getChatId(), Context.MAIN_MENU);
            sendMainMenu(message);
        } else {
            String replyText = messages.getString("cannotUnderstand") + ' ' + messages.getString("chooseDay");
            reply(message, replyText);
        }
    }

//    private void sendResponseOnChooseDay(Message inMessage) {
//        SendMessage outMessage = new SendMessage();
//        outMessage.setChatId(inMessage.getChatId());
//        outMessage.setText(messages.getString("chooseTime"));
//        outMessage.setReplyMarkup(Keyboards.getReturnToMenuKeyboard());
//        send(outMessage);
//    }

//    private void handleMessageFromRemoveSubject(Message message) {
//        if (buttons.getString("back").equals(message.getText())) {
//            sendMainMenu(message);
//            setContext(message.getChatId(), Context.MAIN_MENU);
//        } else {
//            removeSubject(message);
//        }
//    }

//    private void removeSubject(Message inMessage) {
//
//    }

//    private void handleMessageFromAddSubject(Message message) {
//        if (buttons.getString("back").equals(message.getText())) {
//            sendMainMenu(message);
//            setContext(message.getChatId(), Context.MAIN_MENU);
//        } else {
//            addSubject(message);
//            setContext(message.getChatId(), Context.MAIN_MENU);
//        }
//    }

//    private void addSubject(Message inMessage) {
//
//        sendMainMenu(inMessage, messages.getString("subjectAdded"));
//    }

    private void handleMessageFromMainMenu(Message message) {
//        if (buttons.getString("addEvent").equals(inMessage.getText())) {
//            sendResponseOnAddEvent(inMessage);
//            setContext(inMessage.getChatId(), Context.ADD_EVENT_CHOOSE_DATE);
//        } else
        if (buttons.getString("addClass").equals(message.getText())) {
            List<String> subjects = getSubjects(message.getChatId());
            reply(message, messages.getString("chooseSubject"), Keyboards.getSubjectListKeyboard(subjects));
            setContext(message.getChatId(), Context.ADD_CLASS_CHOOSE_SUBJECT);
//        } else if (buttons.getString("addSubject").equals(inMessage.getText())) {
//            sendResponseOnAddSubject(inMessage);
//            setContext(inMessage.getChatId(), Context.ADD_SUBJECT);
//        } else if (buttons.getString("removeSubject").equals(inMessage.getText())) {
//            sendResponseOnRemoveSubject(inMessage);
//            setContext(inMessage.getChatId(), Context.REMOVE_SUBJECT);
        } else if ("/start".equals(message.getText())) {//TODO: use dao
            sendMainMenu(message);
        } else {
            sendMainMenu(message, messages.getString("cannotUnderstand") + messages.getString("chooseFromMenu"));
        }
    }

//    private void sendResponseOnAddEvent(Message inMessage) {
//        SendMessage outMessage = new SendMessage();
//        outMessage.setChatId(inMessage.getChatId());
//        outMessage.setText(messages.getString("chooseDate1"));
//        outMessage.setReplyMarkup(Keyboards.getReturnToMenuKeyboard());
//        send(outMessage);
//        outMessage = new SendMessage();
//        outMessage.setChatId(inMessage.getChatId());
//        outMessage.setText(messages.getString("chooseDate2"));
//        outMessage.setReplyMarkup(Keyboards.getCalendarKeyboard());
//        send(outMessage);
//    }

//    private void sendResponseOnRemoveSubject(Message inMessage) {
//        SendMessage outMessage = new SendMessage();
//        outMessage.setChatId(inMessage.getChatId());
//        outMessage.setText(messages.getString("noSubjects"));
//        outMessage.setReplyMarkup(Keyboards.getAddSubjectKeyboard());
//        //outMessage.setReplyMarkup(Keyboards.getSubjectListKeyboard(subjects));
//        send(outMessage);
//    }

//    private void sendResponseOnAddSubject(Message inMessage) {
//        SendMessage outMessage = new SendMessage();
//        outMessage.setChatId(inMessage.getChatId());
//        outMessage.setText(messages.getString("typeSubject"));
//        outMessage.setReplyMarkup(Keyboards.getAddSubjectKeyboard());
//        send(outMessage);
//    }


    private boolean validDayOfWeek(String string) {
        for (String dayKey : days.keySet()) {
            if (days.getString(dayKey).equals(string)) {
                return true;
            }
        }
        return false;
    }

    private void setContext(Long id, Context addSubject) {
        ContextHolder.getInstance().setContext(id, addSubject);
    }

    private void setEditingValue(Long id, Object value) {
        ContextHolder.getInstance().setEditingValue(id, value);
    }

    private Object getEditingValue(Long id) {
        return ContextHolder.getInstance().getEditingValue(id);
    }

    private void reply(Message inMessage, String text) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(text);
        send(outMessage);
    }

    private void reply(Message inMessage, String text, ReplyKeyboard keyboard) {
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(inMessage.getChatId());
        outMessage.setText(text);
        outMessage.setReplyMarkup(keyboard);
        send(outMessage);
    }

    private void sendMainMenu(Message inMessage) {
        sendMainMenu(inMessage, messages.getString("chooseOption"));
    }

    private void sendMainMenu(Message inMessage, String outMessageText) {
        reply(inMessage, outMessageText, Keyboards.getMainMenuKeyboard());
    }

    private void send(SendMessage outMessage) {
        try {
            execute(outMessage);
        } catch (TelegramApiException e) {
            BotLogger.severe(LOGTAG, e);
        }
    }

}