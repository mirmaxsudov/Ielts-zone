package uz.ieltszone.writequestionsbot.service.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.ieltszone.writequestionsbot.config.BotConfiguration;
import uz.ieltszone.writequestionsbot.entity.Attachment;
import uz.ieltszone.writequestionsbot.entity.User;
import uz.ieltszone.writequestionsbot.entity.enums.LearningCenter;
import uz.ieltszone.writequestionsbot.entity.enums.Task;
import uz.ieltszone.writequestionsbot.entity.enums.UserRole;
import uz.ieltszone.writequestionsbot.entity.request.ApplicationRequest;
import uz.ieltszone.writequestionsbot.repository.AttachmentRepository;
import uz.ieltszone.writequestionsbot.service.base.UserService;
import uz.ieltszone.writequestionsbot.service.bot.enums.Operation;
import uz.ieltszone.writequestionsbot.service.bot.replyMarkups.ReplyMarkup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class TelegramBotService extends TelegramLongPollingBot implements ReplyMarkup {
    private final BotConfiguration botConfiguration;
    private final GroupSenderService groupSenderService;
    private final BotNotificationService botNotificationService;
    private final UserService userService;

    @Value("${bot.group.id}")
    private static String GROUP_ID;

    private static final String BASE_URL = "/home/ielts_zone/bot/write_questions_bot";
    private final AttachmentRepository attachmentRepository;

    @Autowired
    public TelegramBotService(BotConfiguration botConfiguration, GroupSenderService groupSenderService, BotNotificationService botNotificationService, UserService userService, AttachmentRepository attachmentRepository) {
        this.botConfiguration = botConfiguration;
        this.groupSenderService = groupSenderService;
        this.botNotificationService = botNotificationService;
        this.attachmentRepository = attachmentRepository;
        this.userService = userService;

        List<BotCommand> commands = List.of(
                new BotCommand("/start", "Start the bot🔰"),
                new BotCommand("/info", "Get info regarding Bot🤖")
        );

        try {
            execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
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
        try {
            if (update.hasMessage()) {
                processUpdate(update.getMessage());
            }
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
        }
    }

    protected static Map<Long, Operation> MP = new HashMap<>();
    private final Map<Long, ApplicationRequest> APPLICATION_REQUEST = new HashMap<>();

    private void processUpdate(Message message) {
        final Long chatId = message.getChatId();

        log.info("ChatId: {} Message: {}", chatId, message.getText());

        if (chatId.toString().equals(GROUP_ID) || chatId < 0)
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

        if (message.hasDocument()) {
            documentHandler(operation, chatId, message);
            return;
        }

        if (message.hasPhoto()) {
            photoHandler(operation, chatId, message);
            return;
        }

        if (text.equals("/info")) {
            SendMessage info = new SendMessage();
            info.setChatId(chatId);
            info.setText("This bot helps to collect student's IELTS writing exam question. If you take a exam recently, please use /start command to help other candidates.👨‍🏫");

            customSender(info);
            return;
        }

        boolean isUsed;

        if (text.equals("/start")) {
            MP.remove(chatId);
            isUsed = switch (user.getRole()) {
                case ADMIN -> greetingForAdmin(chatId);
                case TEACHER -> greetingForTeacher(chatId);
                case STUDENT -> greetingForStudent(chatId);
            };
        } else if (text.equals("Back")) {
            MP.remove(chatId);
            APPLICATION_REQUEST.remove(chatId);

            switch (user.getRole()) {
                case ADMIN -> greetingForAdmin(chatId);
                case TEACHER -> greetingForTeacher(chatId);
                case STUDENT -> greetingForStudent(chatId);
            }
            return;
        }

        isUsed = switch (user.getRole()) {
            case ADMIN -> adminMenu(chatId, text);
            default -> false;
        };

        if (isUsed)
            return;

        if (text.equals(LearningCenter.IDP.name()) || text.equals(LearningCenter.BRITISH_CONSUL.name())) {
            ApplicationRequest request = ApplicationRequest.builder()
                    .chatId(chatId)
                    .createdAt(Instant.now())
                    .center(LearningCenter.valueOf(text))
                    .studentId(user.getId())
                    .attachmentsUrlsForTask1(new ArrayList<>())
                    .build();

            APPLICATION_REQUEST.put(chatId, request);

            SendMessage askTask = new SendMessage();
            askTask.setChatId(chatId);
            askTask.setText("Do you want to start with Task 1 or Task 2?");
            askTask.setReplyMarkup(getReplyForTask());

            customSender(askTask);
            MP.put(chatId, Operation.ASK_FOR_TASK);
            return;
        } else if (text.equals("Send to Admins")) {
            ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
            groupSenderService.sendApplicationToGroupWithPhotos(request);

            SendMessage info = new SendMessage();
            info.setChatId(chatId);
            info.setReplyMarkup(getReplyForUploadForCenter());
            info.setText("Thank you for sharing all the details! Your information is valuable and will help future test-takers. Have a great day!");
            customSender(info);

            MP.remove(chatId);
            APPLICATION_REQUEST.remove(chatId);
        }

        if (operation == null) return;

        switch (operation) {
            case ASK_FOR_TASK -> askForTask(chatId, text);
            case ASK_FOR_WHEN_TIME -> askForTime(chatId, text);
            case ASK_FOR_QUESTION -> askForQuestion(chatId, text);
            case ASK_FOR_QUESTION_TASK_1 -> askForQuestionTask1FromTask2(chatId, text);
            case ASK_FOR_PHOTO_TASK_1 -> askForPhotoTask1FromTask2(chatId, text);
            case ASK_FOR_QUESTION_FOR_TASK_1 -> askForQuestionForTask1(chatId, text);
            case ASK_FOR_TASK_1_EXISTS -> askForTask1Exists(chatId, text);
            case ASK_FOR_TASK_2_QUESTION_FROM_TASK_1 -> askForQuestionForTask2FromTask1(chatId, text);
            case ASK_FOR_TASK_2_QUESTION_FROM_TASK_1_PHOTO -> askForPhotoForTask2FromTask1(chatId, text);
            // for notification
            case ASK_NOTIFICATION_BODY -> askNotificationBody(chatId, text);
            case ASK_WILL_SEND_TO_FOR_NOTIFICATION -> askWillSendToForNotification(chatId, text);
            case ASK_FOR_PHOTO_EXIST_FOR_NOTIFICATION -> askForPhotoExistForNotification(chatId, text);
        }
    }

    private void askForPhotoExistForNotification(Long chatId, String photoExists) {
        if (photoExists.equalsIgnoreCase("yes")) {
            botNotificationService.askForPhoto(chatId);
        } else {
            botNotificationService.askToPerformToSendToWithoutPhoto(chatId);
        }
    }

    private void askWillSendToForNotification(Long chatId, String willSendToRole) {
        botNotificationService.askWillSendTo(chatId, willSendToRole);
    }

    private void askNotificationBody(Long chatId, String notificationBody) {
        botNotificationService.askNotificationBody(chatId, notificationBody);
    }

    private boolean adminMenu(Long chatId, String text) {

        log.info("Admin menu: " + text);

        switch (text) {
            case "Manage notification 🔔" -> botNotificationService.manageNotification(chatId);
            case "Create notification 🔔" -> botNotificationService.createNotification(chatId);
        }

        return false;
    }

    private void photoHandler(Operation operation, Long chatId, Message message) {
        if (operation != Operation.ASK_FOR_PHOTO && operation != Operation.ASK_FOR_PHOTO_TASK_1_BEFORE_TASK_2) {
            SendMessage error = new SendMessage();
            error.setChatId(chatId);
            error.setText("Why you send me a photo");
            customSender(error);
            return;
        }

        SendMessage info = new SendMessage();
        info.setChatId(chatId);
        info.setText("Please wait until the photo is downloaded👨‍🏫");
        customSender(info);

        String fileId = message.getPhoto().get(message.getPhoto().size() - 1).getFileId();

        Attachment attachment = downloadToStorage(fileId, chatId);

        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        List<Attachment> attachmentsUrlsForTask1 = request.getAttachmentsUrlsForTask1();
        attachmentsUrlsForTask1.add(attachment);
        request.setAttachmentsUrlsForTask1(attachmentsUrlsForTask1);

        if (operation == Operation.ASK_FOR_PHOTO_TASK_1_BEFORE_TASK_2) {
            SendMessage nextTask2Question = new SendMessage();
            nextTask2Question.setChatId(chatId);
            nextTask2Question.setReplyMarkup(getReplyForAnswerAndQuestion());
            nextTask2Question.setText("Thank you! Now, could you please share the question or topic you were given for Writing Task 2?");
            customSender(nextTask2Question);

            MP.put(chatId, Operation.ASK_FOR_TASK_2_QUESTION_FROM_TASK_1_PHOTO);
        } else {
            SendMessage success = new SendMessage();
            success.setChatId(chatId);
            success.setText("Photo downloaded. You can upload another one or press 'Send to Admins'");
            success.setReplyMarkup(getReplyForPhoto());
            customSender(success);
        }
    }

    private void askForPhotoForTask2FromTask1(Long chatId, String question) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setTask2Question(question);

        groupSenderService.sendApplicationToGroupWithPhotos(request);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setReplyMarkup(getReplyForUploadForCenter());
        message.setText("Thank you for sharing all the details! Your information is valuable and will help future test-takers. Have a great day!");

        customSender(message);

        MP.remove(chatId);
        APPLICATION_REQUEST.remove(chatId);
    }

    private void askForQuestionForTask2FromTask1(Long chatId, String question) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setTask2Question(question);

        SendMessage thanks = new SendMessage();
        thanks.setChatId(chatId);
        thanks.setReplyMarkup(getReplyForUploadForCenter());
        thanks.setText("Thank you for sharing all the details! Your information is valuable and will help future test-takers. Have a great day!");

        customSender(thanks);

        groupSenderService.sendApplicationToGroupWithPhotos(request);
        MP.remove(chatId);
        APPLICATION_REQUEST.remove(chatId);
    }

    private void askForTask1Exists(Long chatId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);

        if (text.equals("Yes")) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Please send your files");
            message.setReplyMarkup(deleteReply());
            request.setIsPhotosExistForTask1(true);

            customSender(message);

            MP.put(chatId, Operation.ASK_FOR_PHOTO_TASK_1_BEFORE_TASK_2);
        } else {
            SendMessage nextTask2 = new SendMessage();
            nextTask2.setChatId(chatId);
            nextTask2.setText("Thank you! Now, could you please share the question or topic you were given for Writing Task 2?");
            nextTask2.setReplyMarkup(getReplyForAnswerAndQuestion());
            customSender(nextTask2);

            MP.put(chatId, Operation.ASK_FOR_TASK_2_QUESTION_FROM_TASK_1);
        }
    }

    private void askForQuestionForTask1(Long chatId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setTask1Question(text);

        SendMessage success = new SendMessage();
        success.setChatId(chatId);
        success.setText("Do you have any photos attached for Task 1?");
        success.setReplyMarkup(getReplyForAskPhoto());
        customSender(success);

        MP.put(chatId, Operation.ASK_FOR_TASK_1_EXISTS);
    }

    private void documentHandler(Operation operation, Long chatId, Message message) {
        if (operation != Operation.ASK_FOR_PHOTO && operation != Operation.ASK_FOR_PHOTO_TASK_1_BEFORE_TASK_2) {
            SendMessage error = new SendMessage();
            error.setChatId(chatId);
            error.setText("Why you send me a document");
            customSender(error);
            return;
        }

        SendMessage info = new SendMessage();
        info.setChatId(chatId);
        info.setText("Please wait until the document is downloaded👨‍🏫");
        customSender(info);

        String fileId = message.getDocument().getFileId();

        Attachment attachment = downloadToStorage(fileId, chatId);

        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        List<Attachment> attachmentsUrlsForTask1 = request.getAttachmentsUrlsForTask1();
        attachmentsUrlsForTask1.add(attachment);
        request.setAttachmentsUrlsForTask1(attachmentsUrlsForTask1);

        if (operation == Operation.ASK_FOR_PHOTO_TASK_1_BEFORE_TASK_2) {
            SendMessage nextTask2Question = new SendMessage();
            nextTask2Question.setChatId(chatId);
            nextTask2Question.setReplyMarkup(getReplyForAnswerAndQuestion());
            nextTask2Question.setText("Thank you! Now, could you please share the question or topic you were given for Writing Task 2?");
            customSender(nextTask2Question);

            MP.put(chatId, Operation.ASK_FOR_TASK_2_QUESTION_FROM_TASK_1_PHOTO);
        } else {
            SendMessage success = new SendMessage();
            success.setChatId(chatId);
            success.setText("Photo downloaded. You can upload another one or press 'Send to Admins'");
            success.setReplyMarkup(getReplyForPhoto());
            customSender(success);
        }
    }

    @SneakyThrows
    private Attachment downloadToStorage(String fileId, Long chatId) {
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);

            var file = execute(getFile);
            String filePath = file.getFilePath();

            String url = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;

            RestTemplate restTemplate = new RestTemplate();
            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

            assert imageBytes != null;

            String name = "photo_" + chatId + "_" + Instant.now().getEpochSecond() + getExtension(filePath);
            String fileName = BASE_URL + "\\" + name;

            Files.write(Paths.get(fileName), imageBytes);

            Attachment attachment = new Attachment();
            attachment.setFilePath(fileName);
            attachment.setFileName(name);

            attachmentRepository.saveAndFlush(attachment);

            return attachment;
        } catch (TelegramApiException | IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private String getExtension(String filePath) {
        return filePath.substring(filePath.lastIndexOf(".")); // -> for example ".jpg"
    }

    private void askForPhotoTask1FromTask2(Long chatId, String exists) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);

        if (exists.equals("Yes")) {
            request.setIsPhotosExistForTask1(true);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Please send your files");
            message.setReplyMarkup(deleteReply());

            customSender(message);
            MP.put(chatId, Operation.ASK_FOR_PHOTO);
        } else {
            groupSenderService.sendApplicationToGroupWithPhotos(request);
            SendMessage end = new SendMessage();
            end.setChatId(chatId);
            end.setReplyMarkup(getReplyForUploadForCenter());
            end.setText("Thank you for sharing all the details! Your information is valuable and will help future test-takers. Have a great day!");
            customSender(end);

            MP.remove(chatId);
            APPLICATION_REQUEST.remove(chatId);
        }
    }

    private void askForQuestionTask1FromTask2(Long chatId, String question) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setTask1Question(question);

        SendMessage askPhotoExists = new SendMessage();
        askPhotoExists.setChatId(chatId);
        askPhotoExists.setText("Do you have any photos attached for Task 1?");
        askPhotoExists.setReplyMarkup(getReplyForAskPhoto());

        customSender(askPhotoExists);
        MP.put(chatId, Operation.ASK_FOR_PHOTO_TASK_1);
    }

    private void askForQuestion(Long chatId, String question) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);

        if (request.isTask1Doing()) {
            request.setTask1Question(question);
        } else {
            request.setTask2Question(question);
            request.setTask1Doing(true);

            SendMessage askQuestionTask1 = new SendMessage();
            askQuestionTask1.setChatId(chatId);
            askQuestionTask1.setText("Thank you! Now, could you please share the question or topic you were given for Writing Task 1?");
            askQuestionTask1.setReplyMarkup(getReplyForAnswerAndQuestion());

            customSender(askQuestionTask1);
            MP.put(chatId, Operation.ASK_FOR_QUESTION_TASK_1);
        }
    }

    private void askForTime(Long chatId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setExamDate(text);

        String task;

        if (request.isTask1Doing())
            task = "Task 1";
        else
            task = "Task 2";

        SendMessage askForQuestion = new SendMessage();
        askForQuestion.setChatId(chatId);
        askForQuestion.setText("Thank you! Now, could you please share the question or topic you were given for Writing " + task);
        askForQuestion.setReplyMarkup(getReplyForAnswerAndQuestion());

        customSender(askForQuestion);

        if (request.isTask2Doing())
            MP.put(chatId, Operation.ASK_FOR_QUESTION);
        else
            MP.put(chatId, Operation.ASK_FOR_QUESTION_FOR_TASK_1);
    }

    private void askForTask(Long chatId, String text) {
        if (!(text.equals(Task.TASK_1.name()) || text.equals(Task.TASK_2.name()))) {
            SendMessage error = new SendMessage();
            error.setChatId(chatId);
            error.setText("Please select Task 1 or Task 2");
            error.setReplyMarkup(getReplyForTask());
            customSender(error);
            return;
        }

        Task chosen = Task.valueOf(text);

        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);

        if (chosen == Task.TASK_1) {
            request.setTask1Doing(true);
            request.setTask1(chosen);
        } else {
            request.setTask2Doing(true);
            request.setTask2(chosen);
        }

        SendMessage askWhenTime = new SendMessage();
        askWhenTime.setChatId(chatId);
        askWhenTime.setReplyMarkup(getReplyForAnswerAndQuestion());
        askWhenTime.setText("Let's start. Could you please tell me the date when you took your IELTS exam? (28 April 2024)");

        customSender(askWhenTime);
        MP.put(chatId, Operation.ASK_FOR_WHEN_TIME);
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
        message.setText("Please select where you took your IELTS exam: IDP or British Council?");
        message.setReplyMarkup(getReplyForUploadForCenter());
        customSender(message);
        return true;
    }

    private boolean greetingForAdmin(Long chatId) {
        SendMessage adminMSG = new SendMessage();
        adminMSG.setChatId(chatId);
        adminMSG.setText("Choose");
        adminMSG.setReplyMarkup(getReplyForAdminMenu());

        customSender(adminMSG);
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

        User user = User.builder().chatId(chatId).phoneNumber(phoneNumber).firstName(contact.getFirstName()).lastName(contact.getLastName()).username(from.getUserName()).role(UserRole.STUDENT).isBlock(false).build();

        userService.save(user);
        SendMessage success = new SendMessage();
        success.setChatId(chatId);
        success.setText("You are successfully registered to the bot.\nChoose");
        success.setReplyToMessageId(messageId);
        success.setReplyMarkup(getReplyForUploadForCenter());
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