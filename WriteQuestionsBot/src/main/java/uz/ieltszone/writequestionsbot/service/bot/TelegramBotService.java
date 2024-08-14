package uz.ieltszone.writequestionsbot.service.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.ieltszone.writequestionsbot.config.BotConfiguration;
import uz.ieltszone.writequestionsbot.entity.User;
import uz.ieltszone.writequestionsbot.entity.enums.UserRole;
import uz.ieltszone.writequestionsbot.entity.request.ApplicationRequest;
import uz.ieltszone.writequestionsbot.service.base.LearningCenterService;
import uz.ieltszone.writequestionsbot.service.base.UserService;
import uz.ieltszone.writequestionsbot.service.bot.enums.Operation;
import uz.ieltszone.writequestionsbot.service.bot.replyMarkups.ReplyMarkup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot implements ReplyMarkup {
    private final BotConfiguration botConfiguration;
    private final GroupSenderService groupSenderService;
    private final LearningCenterService learningCenterService;
    private final UserService userService;

    @Autowired
    public TelegramBotService(BotConfiguration botConfiguration, GroupSenderService groupSenderService, LearningCenterService learningCenterService, UserService userService) {
        this.botConfiguration = botConfiguration;
        this.groupSenderService = groupSenderService;
        this.learningCenterService = learningCenterService;
        this.userService = userService;

        List<BotCommand> commands = List.of(
                new BotCommand("/start", "Start the bot🔰"),
                new BotCommand("/info", "Get info regarding Bot🤖"),
                new BotCommand("/help", "Find help🆘"));

        try {
            execute(new SetMyCommands(commands, new BotCommandScopeChat(), null));
        } catch (Exception ignored) {
        }
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);

        try {
            if (update.hasMessage()) {
                processUpdate(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                processCallbackQuery(update.getCallbackQuery());
            }
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
        }
    }

    private final Map<Long, Operation> MP = new HashMap<>();
    private final Map<Long, ApplicationRequest> APPLICATION_REQUEST = new HashMap<>();

    private void processCallbackQuery(CallbackQuery callbackQuery) {

    }


    private void processUpdate(Message message) {
        final Long chatId = message.getChatId();

        if (chatId.equals(-1L))
            return;

        final String text = message.getText();
        final int messageId = message.getMessageId();
        User user = userService.getByChatId(chatId);

        final Operation operation = MP.get(chatId);

        if (user == null) {
            if (operation == null) {
                login(chatId, messageId);
                return;
            }

            if (operation == Operation.LOGIN_PHONE_NUMBER) {
                login(chatId, messageId, message);
                return;
            }

            return;
        }

        boolean isUsed = false;

        if (operation == null) {
            if (text.equals("/start")) {
                isUsed = switch (user.getRole()) {
                    case ADMIN -> greetingForAdmin(chatId);
                    case TEACHER -> greetingForTeacher(chatId);
                    case STUDENT -> greetingForStudent(chatId);
                };
            } else if (text.equals("Upload")) {
                isUsed = uploadQuestion(chatId, messageId);
            }
        }

        if (isUsed)
            return;


        switch (Objects.requireNonNull(operation)) {
            case ASK_FOR_LEARNING_CENTER -> askApplicationCategory(chatId, messageId, text);
        }
    }

    private void askApplicationCategory(Long chatId, int messageId, String learningCenter) {
        boolean exists = learningCenterService
                .getAll()
                .stream()
                .anyMatch(learningCenter1 -> learningCenter1.getName().equals(learningCenter));

        if (!exists) {
            SendMessage error = new SendMessage();
            error.setText("Select correct learning center again");
            error.setChatId(chatId);
            error.setReplyToMessageId(messageId);
            customSender(error);
            return;
        }
    }

    private boolean uploadQuestion(Long chatId, int messageId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose learning category");
        message.setReplyMarkup(getReplyForUploadForCenter(
                learningCenterService.getAll()
        ));
        message.setReplyToMessageId(messageId);
        customSender(message);

        MP.put(chatId, Operation.ASK_FOR_LEARNING_CENTER);
        return true;
    }

    private boolean greetingForTeacher(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose");
        customSender(message);
        return true;
    }

    private boolean greetingForStudent(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose");
        message.setReplyMarkup(getReplyForStudent());
        customSender(message);
        return true;
    }

    private boolean greetingForAdmin(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose");
        customSender(message);
        return true;
    }

    private void login(Long chatId, int messageId, Message message) {
        MP.remove(chatId);

        Contact contact = message.getContact();
        org.telegram.telegrambots.meta.api.objects.User from = message.getFrom();

        String phoneNumber = contact.getPhoneNumber();
        if (userService.existsByPhoneNumber(phoneNumber)) {
            SendMessage error = new SendMessage();
            error.setChatId(chatId);
            error.setText("Aleary registered. Please login with another phone number");
            error.setReplyToMessageId(messageId);
            customSender(error);
            return;
        }

        User user = User.builder()
                .chatId(chatId)
                .phoneNumber(phoneNumber)
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .username(from.getUserName())
                .role(UserRole.STUDENT)
                .isBlock(false)
                .build();

        userService.save(user);
        SendMessage success = new SendMessage();
        success.setChatId(chatId);
        success.setText("You are successfully registered. Please /start the bot again to start using it 🤖");
        success.setReplyToMessageId(messageId);
        success.setReplyMarkup(deleteReply());
        customSender(success);
    }

    private void login(Long chatId, int messageId) {
        SendMessage message = new SendMessage();
        message.setReplyMarkup(replyForLoginPhoneNumber());
        message.setText("Hi, welcome to our bot. Please enter your phone number");
        message.setChatId(chatId);
        message.setReplyToMessageId(messageId);

        customSender(message);
        MP.put(chatId, Operation.LOGIN_PHONE_NUMBER);
    }

    private void customSender(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}