package uz.ieltszone.writequestionsbot.service.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import uz.ieltszone.writequestionsbot.config.BotConfiguration;
import uz.ieltszone.writequestionsbot.entity.Attachment;
import uz.ieltszone.writequestionsbot.entity.User;
import uz.ieltszone.writequestionsbot.entity.request.ApplicationRequest;
import uz.ieltszone.writequestionsbot.repository.AttachmentRepository;
import uz.ieltszone.writequestionsbot.service.base.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class GroupSenderService extends DefaultAbsSender {
    private final BotConfiguration botConfiguration;
    private final UserService userService;
    private final AttachmentRepository attachmentRepository;
    @Value("${bot.group.id}")
    private String GROUP_ID;

    private final static String BASE_FILE_URL = "C:\\Abdurahmon\\Photo & Videos\\Bot";

    protected GroupSenderService(BotConfiguration botConfiguration, UserService userService, AttachmentRepository attachmentRepository) {
        super(new DefaultBotOptions());
        this.botConfiguration = botConfiguration;
        this.userService = userService;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getBotToken();
    }

    @SneakyThrows
    public void sendApplicationToGroupWithoutPhotos(ApplicationRequest request) {
        String url = generateTxtFileStudent(request);

        User user = userService.getByChatId(request.getStudentChatId());

        SendDocument document = new SendDocument();
        document.setDocument(
                new InputFile(
                        new File(url),
                        generateFileName(user)
                )
        );
        document.setChatId(GROUP_ID);
        document.setParseMode(ParseMode.MARKDOWN);
        document.setCaption(" adfsad");

        execute(document);

        CompletableFuture.runAsync(
                () -> deleteFile(url)
        );
    }

    @SneakyThrows
    public void sendApplicationToGroupWithPhotos(ApplicationRequest request) {
        String url = generateTxtFileStudent(request);
        User user = userService.getByChatId(request.getStudentChatId());

        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setChatId(GROUP_ID);

        System.out.println(request.getAttachments());

        List<Long> attachments = request.getAttachments();

        InputMediaPhoto media1 = new InputMediaPhoto();
        media1.setMedia(new File(attachmentRepository.findById(attachments.get(0)).get().getFilePath()), "img1");

        InputMediaPhoto media2 = new InputMediaPhoto();
        media2.setMedia(new File(attachmentRepository.findById(attachments.get(1)).get().getFilePath()), "img2");

        mediaGroup.setMedias(List.of(media1, media2));

        Message message = execute(mediaGroup).get(new Random().nextInt(request.getAttachments().size()));


        SendDocument document = new SendDocument();
        document.setReplyToMessageId(message.getMessageId());
        document.setCaption("dfads");

        document.setDocument(
                new InputFile(
                        new File(url),
                        generateFileName(user)
                )
        );
        document.setChatId(GROUP_ID);
        document.setParseMode(ParseMode.MARKDOWN);
        execute(document);
    }

    private String generateFileCaption(ApplicationRequest request, User user) {
        return "#" + request.getTask() + " #" + request.getLearningCenter() + "\n" +
                "```Short-infoℹ️\n" +
                "FIO - " + user.getFirstName() + "\n" +
                "Submitted time - " + request.getWhenTime() + "\n" +
                "\n ```";
    }

    private String generateFileName(User user) {
        return user.getFirstName() + "-" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        ) + ".txt";
    }

    private void deleteFile(String url) {
        Path path = Paths.get(url);
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @SneakyThrows(IOException.class)
    public String generateTxtFileStudent(ApplicationRequest request) {
        Path base = Paths.get(BASE_FILE_URL);
        if (!Files.exists(base)) {
            Files.createDirectories(base);
        }

        String fileName = System.currentTimeMillis() + "_" + request.getStudentChatId() + ".txt";

        StringBuilder sb = new StringBuilder();
        sb.append("Test Content for Student Chat ID: ").append(request.getStudentChatId()).append("\n");

        String absoluteUrl = BASE_FILE_URL + "\\" + fileName;
        Path path = Paths.get(absoluteUrl);

        Files.createDirectories(path.getParent());
        Files.write(path, sb.toString().getBytes());

        return absoluteUrl;
    }
}