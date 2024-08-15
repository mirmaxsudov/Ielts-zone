package uz.ieltszone.writequestionsbot.service.bot.replyMarkups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.ieltszone.writequestionsbot.entity.enums.LearningCenter;
import uz.ieltszone.writequestionsbot.entity.enums.Task;

import java.util.LinkedList;
import java.util.List;

public interface ReplyMarkup {
    default ReplyKeyboardMarkup replyForLoginPhoneNumber() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);

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

        KeyboardRow rw1 = new KeyboardRow();

        rw1.add("Upload");

        markup.setKeyboard(List.of(rw1));

        return markup;
    }

    default ReplyKeyboardMarkup getReplyForUploadForCenter() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);

        List<KeyboardRow> rows = new LinkedList<>();

        for (LearningCenter learningCenter : LearningCenter.values()) {
            KeyboardRow row = new KeyboardRow();
            row.add(learningCenter.name());
            rows.add(row);
        }

        KeyboardRow back = new KeyboardRow();
        back.add("Back");
        rows.add(back);

        markup.setKeyboard(rows);

        return markup;
    }

    default ReplyKeyboardMarkup getReplyForTask() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(Task.TASK_1.name());
        row1.add(Task.TASK_2.name());

        KeyboardRow back = new KeyboardRow();
        back.add("Back");

        markup.setKeyboard(List.of(row1, back));

        return markup;
    }

    default ReplyKeyboardMarkup getReplyForAnswerAndQuestion() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Skip");

        KeyboardRow back = new KeyboardRow();
        back.add("Back");

        markup.setKeyboard(List.of(row1, back));

        return markup;
    }

    default ReplyKeyboardMarkup getReplyForAskPhoto() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);


        KeyboardRow row1 = new KeyboardRow();
        row1.add("Yes");
        row1.add("No");

        markup.setKeyboard(List.of(row1));
        return markup;
    }

    default ReplyKeyboardMarkup getReplyForPhoto() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Send to Admins");

        KeyboardRow back = new KeyboardRow();
        back.add("Back");

        markup.setKeyboard(List.of(row1, back));

        return markup;
    }

    default ReplyKeyboardRemove deleteReply() {
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        remove.setSelective(true);
        remove.setRemoveKeyboard(true);
        return remove;
    }
}