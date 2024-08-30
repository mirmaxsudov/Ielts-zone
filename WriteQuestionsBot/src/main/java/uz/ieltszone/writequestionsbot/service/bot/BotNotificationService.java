package uz.ieltszone.writequestionsbot.service.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.ieltszone.writequestionsbot.service.base.NotificationService;
import uz.ieltszone.writequestionsbot.service.bot.replyMarkups.ReplyMarkup;

@Service
public class BotNotificationService extends DefaultAbsSender implements ReplyMarkup {
    private final NotificationService notificationService;

    protected BotNotificationService(NotificationService notificationService) {
        super(new DefaultBotOptions());
        this.notificationService = notificationService;
    }

    public void manageNotification(Long chatId) {
        SendMessage notificationMSG = new SendMessage();
        notificationMSG.setChatId(chatId);
        notificationMSG.setText("🔔️🔔️🔔️ NOTIFICATION 🔔️🔔️🔔️");
        notificationMSG.setReplyMarkup(getReplyMarkupForNotificationManu());
        customSender(notificationMSG);
    }

    private Message customSender(SendMessage message) {
        try {
            return execute(message);
        } catch (TelegramApiException e) {
            return null;
        }
    }
}