package uz.ieltszone.writequestionsbot.service.bot.replyMarkups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.ieltszone.writequestionsbot.entity.enums.LearningCenter;
import uz.ieltszone.writequestionsbot.entity.enums.Task;
import uz.ieltszone.writequestionsbot.entity.enums.UserRole;

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

    default ReplyKeyboardMarkup getAskNextStep(Task nextTask) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Next to " + nextTask.name());

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Back");

        markup.setKeyboard(List.of(row1, row2));

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

    // for Admin

    default ReplyKeyboardMarkup getReplyForAdminMenu() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);

        KeyboardButton notificationManager = new KeyboardButton();
        notificationManager.setText("Manage notification 🔔");

        KeyboardButton manageUsers = new KeyboardButton();
        manageUsers.setText("Manage users 👥");

        KeyboardRow rw1 = new KeyboardRow();
        rw1.add(notificationManager);
        rw1.add(manageUsers);

        markup.setKeyboard(List.of(rw1));
        return markup;
    }

    default ReplyKeyboardMarkup getReplyMarkupForNotificationManu() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);

        KeyboardButton create = new KeyboardButton();
        create.setText("Create notification 🔔");

        KeyboardButton back = new KeyboardButton();
        back.setText("Back");

        KeyboardRow rw1 = new KeyboardRow();
        KeyboardRow rw2 = new KeyboardRow();
        rw1.add(create);
        rw2.add(back);

        markup.setKeyboard(List.of(rw1, rw2));

        return markup;
    }

    default ReplyKeyboardMarkup getReplyMarkupForWillSendTo(UserRole[] roles) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);

        List<KeyboardRow> rows = new LinkedList<>();

        for (UserRole role : roles) {
            KeyboardRow row = new KeyboardRow();
            row.add(role.name());
            rows.add(row);
        }

        KeyboardRow back = new KeyboardRow();
        back.add("Back");
        rows.add(back);

        markup.setKeyboard(rows);

        return markup;
    }

    default ReplyKeyboardMarkup getReplyMarkupForPhotoExistsForNotification() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Yes");
        row1.add("No");

        markup.setKeyboard(List.of(row1));
        return markup;
    }

    default InlineKeyboardMarkup getPerformToSendInlineMarkup(Long notificationId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        InlineKeyboardButton send = new InlineKeyboardButton();
        send.setText("Send ✅");
        send.setCallbackData("SEND_NOTIFICATION: " + notificationId);

        InlineKeyboardButton remove = new InlineKeyboardButton();
        remove.setText("Remove ❌");
        remove.setCallbackData("REMOVE_NOTIFICATION: " + notificationId);

        markup.setKeyboard(List.of(List.of(send)));

        return markup;
    }
}