package uz.ieltszone.writequestionsbot.service.bot.replyMarkups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.ieltszone.writequestionsbot.entity.LearningCenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public interface ReplyMarkup {
    default ReplyKeyboardMarkup replyForLoginPhoneNumber() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        KeyboardButton phone = new KeyboardButton();
        phone.setRequestContact(true);
        phone.setText("Share phone number📞");

        KeyboardRow rw1 = new KeyboardRow();
        rw1.add(phone);

        markup.setKeyboard(List.of(rw1));

        return markup;
    }

    default ReplyKeyboardMarkup getReplyForStudent() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        KeyboardRow rw1 = new KeyboardRow();
        KeyboardRow rw2 = new KeyboardRow();

        rw1.add("Upload");
        rw2.add("Feedback");

        markup.setKeyboard(List.of(rw1, rw2));

        return markup;
    }

    default ReplyKeyboardMarkup getReplyForUploadForCenter(List<LearningCenter> learningCenters) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = new LinkedList<>();

        for (LearningCenter learningCenter : learningCenters) {
            KeyboardRow row = new KeyboardRow();
            row.add(learningCenter.getName());
            rows.add(row);
        }

        KeyboardRow back = new KeyboardRow();
        back.add("Back");
        rows.add(back);

        markup.setKeyboard(rows);

        return markup;
    }

    default ReplyKeyboardRemove deleteReply() {
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        remove.setSelective(true);
        remove.setRemoveKeyboard(true);
        return remove;
    }
}