package com.intetics.organizerbot.keyboards;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Keyboards {

    private static ResourceBundle buttons = ResourceBundle.getBundle("buttons");
    private static ResourceBundle days = ResourceBundle.getBundle("days");

    public static ReplyKeyboardMarkup getReturnToMenuKeyboard(){
        List<List<String>> rows = new ArrayList<List<String>>(){{
            add(new ArrayList<String>(){{
                add(buttons.getString("mainMenu"));
            }});
        }};
        return makeKeyBoard(rows);
    }

    public static ReplyKeyboardMarkup getMainMenuKeyboard(){
        List<List<String>> rows = new ArrayList<List<String>>(){{
            add(new ArrayList<String>(){{
                add(buttons.getString("addClass"));
            }});
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

    public static ReplyKeyboardMarkup getSubjectListKeyboard(List<String> subjects){
        List<List<String>> rows = new ArrayList<>();
        subjects.forEach(subject -> rows.add(new ArrayList<String>(){{add(subject);}}));
        rows.add(new ArrayList<String>(){{add(buttons.getString("mainMenu"));}});
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

    public static ReplyKeyboard getDaysListKeyboard() {
        List<List<String>> rows = new ArrayList<List<String>>(){{
            add(new ArrayList<String>(){{
                add(days.getString("monday"));
            }});
            add(new ArrayList<String>(){{
                add(days.getString("tuesday"));
            }});
            add(new ArrayList<String>(){{
                add(days.getString("wednesday"));
            }});
            add(new ArrayList<String>(){{
                add(days.getString("thursday"));
            }});
            add(new ArrayList<String>(){{
                add(days.getString("friday"));
            }});
            add(new ArrayList<String>(){{
                add(days.getString("saturday"));
            }});
            add(new ArrayList<String>(){{
                add(days.getString("sunday"));
            }});
            add(new ArrayList<String>(){{
                add(buttons.getString("mainMenu"));
            }});
        }};
        return makeKeyBoard(rows);
    }
}
