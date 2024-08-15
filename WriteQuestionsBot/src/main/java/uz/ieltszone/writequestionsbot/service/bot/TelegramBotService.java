package uz.ieltszone.writequestionsbot.service.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.ieltszone.writequestionsbot.config.BotConfiguration;
import uz.ieltszone.writequestionsbot.entity.Attachment;
import uz.ieltszone.writequestionsbot.entity.User;
import uz.ieltszone.writequestionsbot.entity.enums.LearningCenter;
import uz.ieltszone.writequestionsbot.entity.enums.Task;
import uz.ieltszone.writequestionsbot.entity.enums.UserRole;
import uz.ieltszone.writequestionsbot.entity.request.ApplicationRequest;
import uz.ieltszone.writequestionsbot.repository.AttachmentRepository;
import uz.ieltszone.writequestionsbot.service.base.AttachmentService;
import uz.ieltszone.writequestionsbot.service.base.UserService;
import uz.ieltszone.writequestionsbot.service.bot.enums.Operation;
import uz.ieltszone.writequestionsbot.service.bot.replyMarkups.ReplyMarkup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot implements ReplyMarkup {
    private final BotConfiguration botConfiguration;
    private final GroupSenderService groupSenderService;
    private final UserService userService;
    private final AttachmentService attachmentService;

    private final static String BASE_URL = "C:\\Abdurahmon\\Photo & Videos\\Bot";
    private final AttachmentRepository attachmentRepository;

    @Autowired
    public TelegramBotService(BotConfiguration botConfiguration, GroupSenderService groupSenderService, UserService userService, AttachmentService attachmentService, AttachmentRepository attachmentRepository) {
        this.botConfiguration = botConfiguration;
        this.groupSenderService = groupSenderService;
        this.userService = userService;
        this.attachmentService = attachmentService;

        List<BotCommand> commands = List.of(
                new BotCommand("/start", "Start the bot🔰"),
                new BotCommand("/info", "Get info regarding Bot🤖"),
                new BotCommand("/help", "Find help🆘"));

        try {
            execute(new SetMyCommands(commands, new BotCommandScopeChat(), null));
        } catch (Exception ignored) {
        }
        this.attachmentRepository = attachmentRepository;
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

        System.out.println("chatId = " + chatId);

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

        if (message.hasPhoto()) {
            photoHandler(operation, chatId, message);
            return;
        }

        boolean isUsed = false;

        if (operation == null) {
            if (text.equals("/start")) {
                MP.remove(chatId);
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
            case ASK_FOR_TASK -> askApplicationTask(chatId, messageId, text);
            case ASK_FOR_QUESTION -> askApplicationQuestion(chatId, messageId, text);
            case ASK_FOR_ANSWER -> askApplicationAnswer(chatId, messageId, text);
            case ASK_FOR_WHEN_TIME -> askApplicationWhenTime(chatId, messageId, text);
            case ASK_FOR_PHOTO_EXIST -> askApplicationPhotoExist(chatId, messageId, text);
            case ASK_FOR_PHOTO -> askApplicationPhoto(chatId, messageId, text);
        }
    }

    private void askApplicationPhoto(Long chatId, int messageId, String text) {
        MP.remove(chatId);
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);

        List<Long> attachments = request.getAttachments();
        request.setStudentChatId(chatId);

        if (attachments.isEmpty()) {
            groupSenderService.sendApplicationToGroupWithoutPhotos(request);
            return;
        }

        groupSenderService.sendApplicationToGroupWithPhotos(request);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setReplyMarkup(getReplyForStudent());
        sendMessage.setText("Photo sent successfully");
        customSender(sendMessage);
    }

    private void photoHandler(Operation operation, Long chatId, Message message) {
        if (operation != null && operation != Operation.ASK_FOR_PHOTO)
            return;

        List<PhotoSize> photo = message.getPhoto();
        PhotoSize photoSize = photo.get(photo.size() - 1);
        String fileId = photoSize.getFileId();

        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        List<Long> attachments = request.getAttachments();

        System.out.println("attachments = " + attachments);

        Long attachmentId = downloadPhoto(fileId, chatId, photoSize.getFileUniqueId());
        attachments.add(attachmentId);
    }

    private Long downloadPhoto(String fileId, Long chatId, String fileUniqueId) {
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);

            var file = execute(getFile);
            String filePath = file.getFilePath();

            String url = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;

            RestTemplate restTemplate = new RestTemplate();
            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

            assert imageBytes != null;

            String name = "photo_" + chatId + "_" + Instant.now().getEpochSecond() + "_" + UUID.randomUUID() + "_" + fileUniqueId + ".png";
            String fileName = BASE_URL + "/" + name;
            Files.write(Paths.get(fileName), imageBytes);

            Attachment attachment = new Attachment();
            attachment.setFileName(name);
            attachment.setFilePath(fileName);
            return attachmentRepository.save(attachment).getId();
        } catch (TelegramApiException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private void askApplicationPhotoExist(Long chatId, int messageId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        if (text.equalsIgnoreCase("Yes")) {
            SendMessage message = new SendMessage();
            message.setText("Please, send me your photos");
            message.setChatId(chatId);
            message.setReplyToMessageId(messageId);
            message.setReplyMarkup(getReplyForPhoto());
            customSender(message);

            MP.put(chatId, Operation.ASK_FOR_PHOTO);
        } else if (text.equalsIgnoreCase("No")) {
            request.setStudentChatId(chatId);
            groupSenderService.sendApplicationToGroupWithoutPhotos(request);

            MP.remove(chatId);
            APPLICATION_REQUEST.remove(chatId);
        } else {
            SendMessage error = new SendMessage();
            error.setText("Please, enter Yes or No");
            error.setChatId(chatId);
            error.setReplyToMessageId(messageId);
            customSender(error);
        }
    }

    private void askApplicationWhenTime(Long chatId, int messageId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setWhenTime(text);


        SendMessage message = new SendMessage();
        message.setText("Do you have any photos? Press below one of buttons (Yes/No)");
        message.setChatId(chatId);
        message.setReplyToMessageId(messageId);
        message.setReplyMarkup(getReplyForAskPhoto());
        customSender(message);

        MP.put(chatId, Operation.ASK_FOR_PHOTO_EXIST);
    }

    private void askApplicationAnswer(Long chatId, int messageId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setAnswerAsText(text);

        SendMessage message = new SendMessage();
        message.setText("Enter time when you took the test");
        message.setChatId(chatId);
        message.setReplyToMessageId(messageId);
        message.setReplyMarkup(getReplyForAnswerAndQuestion());
        customSender(message);

        MP.put(chatId, Operation.ASK_FOR_WHEN_TIME);

        System.out.println("request = " + request);
    }

    private void askApplicationQuestion(Long chatId, int messageId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setQuestionAsText(text);

        SendMessage message = new SendMessage();
        message.setText("Write down your answer. Otherwise press below button.");
        message.setChatId(chatId);
        message.setReplyToMessageId(messageId);
        message.setReplyMarkup(getReplyForAnswerAndQuestion());
        customSender(message);

        MP.put(chatId, Operation.ASK_FOR_ANSWER);
    }

    private void askApplicationTask(Long chatId, int messageId, String text) {
        if (!(text.equalsIgnoreCase(Task.TASK_1.name()) || text.equalsIgnoreCase(Task.TASK_2.name()))) {
            SendMessage error = new SendMessage();
            error.setText("Wrong task. Please choose again");
            error.setChatId(chatId);
            error.setReplyToMessageId(messageId);
            customSender(error);
            return;
        }

        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setTask(Task.valueOf(text));

        SendMessage nextStep = new SendMessage();
        nextStep.setChatId(chatId);
        nextStep.setText("Write down your question if you want. Otherwise press below button.");
        nextStep.setReplyMarkup(getReplyForTask());
        nextStep.setReplyMarkup(getReplyForAnswerAndQuestion());
        customSender(nextStep);

        MP.put(chatId, Operation.ASK_FOR_QUESTION);
    }

    private void askApplicationCategory(Long chatId, int messageId, String learningCenter) {
        if (!(learningCenter.equalsIgnoreCase(LearningCenter.IDP.name()) || learningCenter.equalsIgnoreCase(LearningCenter.BRITISH_CONSUL.name()))) {
            SendMessage error = new SendMessage();
            error.setText("Wrong learning center. Please choose again");
            error.setChatId(chatId);
            error.setReplyToMessageId(messageId);
            customSender(error);
            return;
        }

        APPLICATION_REQUEST.put(chatId, ApplicationRequest
                .builder()
                .attachments(new LinkedList<>())
                .build());

        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setLearningCenter(LearningCenter.valueOf(learningCenter));

        MP.put(chatId, Operation.ASK_FOR_TASK);

        SendMessage nextStep = new SendMessage();
        nextStep.setChatId(chatId);
        nextStep.setText("Choose task in which you wrote questions.");
        nextStep.setReplyMarkup(getReplyForTask());
        customSender(nextStep);
    }

    private boolean uploadQuestion(Long chatId, int messageId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose learning category");
        message.setReplyMarkup(getReplyForUploadForCenter());
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