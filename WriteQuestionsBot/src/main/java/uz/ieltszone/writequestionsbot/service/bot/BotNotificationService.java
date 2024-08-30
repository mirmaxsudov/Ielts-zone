package uz.ieltszone.writequestionsbot.service.bot;

import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.ieltszone.writequestionsbot.config.BotConfiguration;
import uz.ieltszone.writequestionsbot.entity.Notification;
import uz.ieltszone.writequestionsbot.entity.User;
import uz.ieltszone.writequestionsbot.entity.enums.UserRole;
import uz.ieltszone.writequestionsbot.entity.request.NotificationRequest;
import uz.ieltszone.writequestionsbot.service.base.NotificationService;
import uz.ieltszone.writequestionsbot.service.base.UserService;
import uz.ieltszone.writequestionsbot.service.bot.enums.Operation;
import uz.ieltszone.writequestionsbot.service.bot.replyMarkups.ReplyMarkup;

import java.util.HashMap;
import java.util.Map;

import static uz.ieltszone.writequestionsbot.service.bot.TelegramBotService.MP;

@Service
public class BotNotificationService extends DefaultAbsSender implements ReplyMarkup {
    private final NotificationService notificationService;
    private final BotConfiguration botConfiguration;
    private final UserService userService;

    private final static Map<Long, NotificationRequest> NOTIFICATION_REQUEST_MAP = new HashMap<>();

    protected BotNotificationService(NotificationService notificationService, BotConfiguration botConfiguration, UserService userService) {
        super(new DefaultBotOptions());
        this.notificationService = notificationService;
        this.botConfiguration = botConfiguration;
        this.userService = userService;
    }


    @Override
    public String getBotToken() {
        return botConfiguration.getBotToken();
    }

    public void manageNotification(Long chatId) {
        SendMessage notificationMSG = new SendMessage();
        notificationMSG.setChatId(chatId);
        notificationMSG.setText("🔔️🔔️🔔️ NOTIFICATION 🔔️🔔️🔔️");
        notificationMSG.setReplyMarkup(getReplyMarkupForNotificationManu());
        customSender(notificationMSG);
    }

    private void customSender(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    public void createNotification(Long chatId) {
        NotificationRequest request = new NotificationRequest();
        request.setChatId(chatId);

        NOTIFICATION_REQUEST_MAP.put(chatId, request);

        SendMessage askBodyForNotification = new SendMessage();
        askBodyForNotification.setChatId(chatId);
        askBodyForNotification.setText("Enter text for notification");

        customSender(askBodyForNotification);

        MP.put(chatId, Operation.ASK_NOTIFICATION_BODY);
    }

    public void askNotificationBody(Long chatId, String notificationBody) {
        NotificationRequest request = NOTIFICATION_REQUEST_MAP.get(chatId);
        request.setBody(notificationBody);

        SendMessage askWillSendTo = new SendMessage();
        askWillSendTo.setChatId(chatId);
        askWillSendTo.setText("Who will be notified?");

        UserRole[] roles = UserRole.values();
        askWillSendTo.setReplyMarkup(getReplyMarkupForWillSendTo(roles));

        customSender(askWillSendTo);

        MP.put(chatId, Operation.ASK_WILL_SEND_TO_FOR_NOTIFICATION);
    }

    public void askWillSendTo(Long chatId, String willSendToRole) {
        NotificationRequest request = NOTIFICATION_REQUEST_MAP.get(chatId);
        request.setWillSendTo(UserRole.valueOf(willSendToRole));

        SendMessage askPhotoExists = new SendMessage();
        askPhotoExists.setChatId(chatId);
        askPhotoExists.setText("Do you have a photo?");
        askPhotoExists.setReplyMarkup(getReplyMarkupForPhotoExistsForNotification());

        customSender(askPhotoExists);

        MP.put(chatId, Operation.ASK_FOR_PHOTO_EXIST_FOR_NOTIFICATION);

    }

    public void askToPerformToSendToWithoutPhoto(Long chatId) {
        NotificationRequest request = NOTIFICATION_REQUEST_MAP.get(chatId);

        Notification notification = notificationService.save(request);

        SendMessage performToSendTo = new SendMessage();
        performToSendTo.setChatId(chatId);

        performToSendTo.setText(
                String.format(
                        """
                                 Notification body - %s
                                                                \s
                                 Are you sure you want to send this notification ? ❔️
                                \s""", request.getBody()
                )
        );

        performToSendTo.setReplyMarkup(getPerformToSendInlineMarkup(notification.getId()));
        customSender(performToSendTo);
    }

    public void askForPhoto(Long chatId) {
        SendMessage photo = new SendMessage();
        photo.setChatId(chatId);
        photo.setText("Send photo");

    }
}