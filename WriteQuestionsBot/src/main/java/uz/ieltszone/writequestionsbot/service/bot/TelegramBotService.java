package uz.ieltszone.writequestionsbot.service.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
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
    private final UserService userService;

    @Value("${bot.group.id}")
    private static String GROUP_ID;

    private static final String BASE_URL = "/home/ielts_zone/bot/write_questions_bot/files";
    private final AttachmentRepository attachmentRepository;

    @Autowired
    public TelegramBotService(BotConfiguration botConfiguration, GroupSenderService groupSenderService, UserService userService, AttachmentRepository attachmentRepository) {
        this.botConfiguration = botConfiguration;
        this.groupSenderService = groupSenderService;
        this.userService = userService;
        this.attachmentRepository = attachmentRepository;

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

    private final Map<Long, Operation> MP = new HashMap<>();
    private final Map<Long, ApplicationRequest> APPLICATION_REQUEST = new HashMap<>();

    private void processUpdate(Message message) {
        final Long chatId = message.getChatId();

        if (chatId.toString().equals(GROUP_ID))
            return;

        if (chatId.equals(-1L)) return;

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

        boolean isUsed = false;

        if (text.equals("/info")) {
            SendMessage info = new SendMessage();
            info.setChatId(chatId);

            customSender(info);
            return;
        }

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
            greetingForStudent(chatId);
            return;
        }
        if (LearningCenter.IDP.name().equals(text) || LearningCenter.BRITISH_CONSUL.name().equals(text)) {
            APPLICATION_REQUEST.put(chatId, ApplicationRequest.builder().learningCenter(LearningCenter.valueOf(text)).studentChatId(chatId).attachments(new ArrayList<>()).build());

            SendMessage task = new SendMessage();

            task.setChatId(chatId);
            task.setReplyMarkup(getReplyForTask());
            task.setText("Do you want to start with Task 1 or Task 2?");

            customSender(task);
            MP.put(chatId, Operation.ASK_FOR_TASK);
            return;
        } else if (text.equals("Send to Admins")) {
            ApplicationRequest request = APPLICATION_REQUEST.get(chatId);

            if (request == null) {
                greetingForStudent(chatId);
                return;
            }

            groupSenderService.sendApplicationToGroupWithPhotos(request);

            SendMessage success = new SendMessage();
            success.setChatId(chatId);
            success.setText("Thank you for your application.");

            customSender(success);

            if (request.getIsSecondOne() == null) {
                Task nextTask = request.getTask().equals(Task.TASK_1) ? Task.TASK_2 : Task.TASK_1;

                success.setText("Great! Now, could you please provide the question or topic you had for Writing " + (nextTask.equals(Task.TASK_1) ? "Task 1" : "Task 2"));
                request.setTask(nextTask);
                request.setIsSecondOne(true);
                success.setReplyMarkup(getReplyForAnswerAndQuestion());

                MP.put(chatId, Operation.ASK_FOR_QUESTION_FOR_SECOND);
                customSender(success);
                return;
            }

            APPLICATION_REQUEST.remove(chatId);
            greetingForStudent(chatId);
            return;
        }

        if (isUsed) return;

        if (operation == null) return;

        switch (operation) {
            case ASK_FOR_TASK -> askApplicationTask(chatId, messageId, text);
            case ASK_FOR_QUESTION -> askApplicationQuestion(chatId, messageId, text);
            case ASK_FOR_WHEN_TIME -> askApplicationWhenTime(chatId, messageId, text);
            case ASK_FOR_PHOTO_EXIST -> askApplicationPhotoExist(chatId, messageId, text);
            case ASK_FOR_QUESTION_FOR_SECOND -> askApplicationQuestionForSecond(chatId, messageId, text);
            case ASK_FOR_PHOTO_EXIST_FOR_SECOND -> askApplicationPhotoExistForSecond(chatId, messageId, text);
        }
    }

    private void askApplicationPhotoExistForSecond(Long chatId, int messageId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setAttachments(new ArrayList<>());
        if (text.equalsIgnoreCase("Yes")) {
            SendMessage message = new SendMessage();
            message.setText("Please, send me your photo or file");
            message.setChatId(chatId);
            message.setReplyToMessageId(messageId);
            message.setReplyMarkup(deleteReply());
            customSender(message);

            MP.put(chatId, Operation.ASK_FOR_PHOTO_FOR_SECOND);
        } else if (text.equalsIgnoreCase("No")) {
            request.setTask(request.getTask().equals(Task.TASK_1) ? Task.TASK_2 : Task.TASK_1);
            groupSenderService.sendApplicationToGroupWithoutPhotos(request);
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Thank you for sharing all the details! Your information is valuable and will help future test-takers. Have a great day!");
            message.setReplyToMessageId(messageId);
            message.setReplyMarkup(getReplyForUploadForCenter());
            customSender(message);

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

    private void askApplicationQuestionForSecond(Long chatId, int messageId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);

        request.setQuestionAsText(text);
        Task task = request.getTask();

        String question = task.equals(Task.TASK_1) ? "Task 1" : "Task 2";

        SendMessage message = new SendMessage();
        message.setText("If there was any additional file or image related to " + question + ", could you please upload it as well?");
        message.setChatId(chatId);
        message.setReplyToMessageId(messageId);
        message.setReplyMarkup(getReplyForAskPhoto());
        customSender(message);

        MP.put(chatId, Operation.ASK_FOR_PHOTO_EXIST_FOR_SECOND);
    }

    private void documentHandler(Operation operation, Long chatId, Message message) {
        if (operation != null && operation != Operation.ASK_FOR_PHOTO && operation != Operation.ASK_FOR_PHOTO_FOR_SECOND)
            return;

        SendMessage info = new SendMessage();
        info.setChatId(chatId);
        info.setText("Please wait until the document is downloaded");

        customSender(info);

        Document document = message.getDocument();
        String fileName = document.getFileName();

        Long attachmentId = downloadDocument(document.getFileId(), chatId, document.getFileUniqueId(), getExtension(fileName));

        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.getAttachments().add(attachmentId);

        SendMessage success = new SendMessage();
        success.setChatId(chatId);
        success.setReplyMarkup(getReplyForPhoto());
        success.setText("Document downloaded ✅. Do you want to upload another one?");
        customSender(success);
    }

    private Long downloadDocument(String fileId, Long chatId, String fileUniqueId, String extension) {
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);

            var file = execute(getFile);
            String filePath = file.getFilePath();

            String url = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;

            RestTemplate restTemplate = new RestTemplate();
            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

            assert imageBytes != null;

            String name = "document_" + chatId + "_" + Instant.now().getEpochSecond() + "_" + UUID.randomUUID() + "_" + fileUniqueId + "." + extension;
            String fileName = BASE_URL + "/" + name;
            Files.write(Paths.get(fileName), imageBytes);

            Attachment attachment = new Attachment();
            attachment.setFileName(name);
            attachment.setFilePath(fileName);
            attachmentRepository.saveAndFlush(attachment);

            return attachment.getId();
        } catch (TelegramApiException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void photoHandler(Operation operation, Long chatId, Message message) {
        if (operation != null &&
                !operation.equals(Operation.ASK_FOR_PHOTO) &&
                !operation.equals(Operation.ASK_FOR_PHOTO_FOR_SECOND))
            return;

        SendMessage info = new SendMessage();
        info.setChatId(chatId);
        info.setText("Please wait until the document is downloaded");

        customSender(info);

        List<PhotoSize> photo = message.getPhoto();
        PhotoSize photoSize = photo.get(photo.size() - 1);
        String fileId = photoSize.getFileId();

        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        List<Long> attachments = request.getAttachments();

        Long attachmentId = downloadPhoto(fileId, chatId, photoSize.getFileUniqueId());
        attachments.add(attachmentId);

        SendMessage success = new SendMessage();
        success.setChatId(chatId);
        success.setText("Photo downloaded. You can upload another one or press 'Send to Admins'");
        success.setReplyMarkup(getReplyForPhoto());
        customSender(success);
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

            attachmentRepository.saveAndFlush(attachment);

            return attachment.getId();
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
            message.setReplyMarkup(deleteReply());
            customSender(message);

            MP.put(chatId, Operation.ASK_FOR_PHOTO);
        } else if (text.equalsIgnoreCase("No")) {
            request.setStudentChatId(chatId);
            groupSenderService.sendApplicationToGroupWithoutPhotos(request);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);

            Task task = request.getTask().equals(Task.TASK_1) ? Task.TASK_2 : Task.TASK_1;
            String nextTask = task.equals(Task.TASK_1) ? "Task 1" : "Task 2";

            message.setText("Great! Now, could you please provide the question or topic you had for Writing " + nextTask + "?");
            request.setTask(task);
            request.setIsSecondOne(true);
            message.setReplyMarkup(getReplyForAnswerAndQuestion());

            MP.put(chatId, Operation.ASK_FOR_QUESTION_FOR_SECOND);
            customSender(message);
        } else {
            SendMessage error = new SendMessage();
            error.setText("Please, choose Yes or No");
            error.setChatId(chatId);
            error.setReplyToMessageId(messageId);
            customSender(error);
        }
    }

    private void askApplicationWhenTime(Long chatId, int messageId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setWhenTime(text);

        Task task = request.getTask();
        String nextTask = task.equals(Task.TASK_1) ? "Task 1" : "Task 2";


        SendMessage message = new SendMessage();
        message.setText("Thank you! Now, could you please share the exact question or topic you were given for Writing " + nextTask + "?");
        message.setChatId(chatId);
        message.setReplyToMessageId(messageId);
        message.setReplyMarkup(getReplyForAnswerAndQuestion());
        customSender(message);

        MP.put(chatId, Operation.ASK_FOR_QUESTION);
    }

    private void askApplicationQuestion(Long chatId, int messageId, String text) {
        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setQuestionAsText(text);

        String nextTask = request.getTask().equals(Task.TASK_1) ? "Task 1" : "Task 2";

        SendMessage message = new SendMessage();
        message.setText("If there was any graph, chart, or image provided with " + nextTask + ", could you please upload a picture or file of it?");
        message.setChatId(chatId);
        message.setReplyToMessageId(messageId);
        message.setReplyMarkup(getReplyForAskPhoto());
        customSender(message);

        MP.put(chatId, Operation.ASK_FOR_PHOTO_EXIST);
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

        Task task = request.getTask();

        SendMessage nextStep = new SendMessage();
        nextStep.setChatId(chatId);
        nextStep.setText("Let's start. Could you please tell me the date when you took your IELTS exam? (28 April 2024)");
        nextStep.setReplyMarkup(getReplyForTask());
        nextStep.setReplyMarkup(getReplyForAnswerAndQuestion());
        customSender(nextStep);

        MP.put(chatId, Operation.ASK_FOR_WHEN_TIME);
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

        APPLICATION_REQUEST.put(chatId, ApplicationRequest.builder().attachments(new LinkedList<>()).build());

        ApplicationRequest request = APPLICATION_REQUEST.get(chatId);
        request.setLearningCenter(LearningCenter.valueOf(learningCenter));

        MP.put(chatId, Operation.ASK_FOR_TASK);

        SendMessage nextStep = new SendMessage();
        nextStep.setChatId(chatId);
        nextStep.setText("Choose task in which you wrote questions.");
        nextStep.setReplyMarkup(getReplyForTask());
        customSender(nextStep);
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