package com.intetics.organizerbot.keyboards;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Keyboards {

    public static ReplyKeyboardMarkup getMainMenuKeyboard(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Add subject");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

}
