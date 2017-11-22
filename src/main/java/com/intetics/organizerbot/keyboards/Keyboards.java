package com.intetics.organizerbot.keyboards;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Keyboards {

    private static ResourceBundle buttons = ResourceBundle.getBundle("buttons");

    public static ReplyKeyboardMarkup getMainMenuKeyboard(){
        List<List<String>> rows = new ArrayList<List<String>>(){{
            add(new ArrayList<String>(){{
                add(buttons.getString("addSubject"));
            }});
            add(new ArrayList<String>(){{
                add(buttons.getString("removeSubject"));
            }});
        }};
        return makeKeyBoard(rows);
    }

    public static ReplyKeyboardMarkup getAddSubjectKeyboard(){
        List<List<String>> rows = new ArrayList<List<String>>(){{
            add(new ArrayList<String>(){{
                add(buttons.getString("back"));
            }});
        }};
        return makeKeyBoard(rows);
    }

    public static ReplyKeyboardMarkup getRemoveSubjectKeyboard(List<String> subjects){
        List<List<String>> rows = new ArrayList<>();
        subjects.forEach(subject -> rows.add(new ArrayList<String>(){{add(subject);}}));
        return makeKeyBoard(rows);
    }

    private static ReplyKeyboardMarkup makeKeyBoard(List<List<String>> rows){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        rows.forEach(row -> addKeyboardRow(keyboard, row));
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private static void addKeyboardRow(List<KeyboardRow> keyboard, List<String> row){
        KeyboardRow keyboardRow = new KeyboardRow();
        row.forEach(item -> keyboardRow.add(item));
        keyboard.add(keyboardRow);
    }

}
