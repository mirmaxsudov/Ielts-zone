package uz.ieltszone.writequestionsbot.service.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import uz.ieltszone.writequestionsbot.config.BotConfiguration;
import uz.ieltszone.writequestionsbot.entity.Attachment;
import uz.ieltszone.writequestionsbot.entity.User;
import uz.ieltszone.writequestionsbot.entity.request.ApplicationRequest;
import uz.ieltszone.writequestionsbot.repository.AttachmentRepository;
import uz.ieltszone.writequestionsbot.service.base.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class GroupSenderService extends DefaultAbsSender {
    private final BotConfiguration botConfiguration;
    private final UserService userService;
    private final AttachmentRepository attachmentRepository;
    @Value("${bot.group.id}")
    private String GROUP_ID;

    private final static String BASE_FILE_URL = "/home/ielts_zone/bot/write_questions_bot";

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
    public void sendApplicationToGroupWithPhotos(ApplicationRequest request) {
        User user = userService.getByChatId(request.getChatId());
        String URL = BASE_FILE_URL + "\\" + user.getFirstName() + "___" + UUID.randomUUID();

        Path target = Paths.get(URL);

        if (!Files.exists(target))
            Files.createDirectory(target);

        String textFileURL = generateTxtFileStudent(request);
        Path sourcePath = Paths.get(textFileURL);

        Path targetPath = target.resolve(sourcePath.getFileName());

        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        for (Attachment attachment : request.getAttachmentsUrlsForTask1()) {
            Path attachmentSourcePath = Paths.get(attachment.getFilePath());
            Path attachmentTargetPath = target.resolve(Paths.get(attachment.getFileName()));

            Files.move(attachmentSourcePath, attachmentTargetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        String outputZipFile = URL + ".zip";
        zipDirectory(URL, outputZipFile);

        SendDocument document = new SendDocument();
        document.setCaption(generateFileCaption(request, user));
        document.setChatId(GROUP_ID);
        document.setDocument(
                new InputFile(
                        new File(outputZipFile),
                        user.getFirstName() + "'exam.zip"
                )
        );
        execute(document);

        CompletableFuture.runAsync(
                () -> {
                    deleteFile(outputZipFile);
                    deleteDirectoryRecursively(Paths.get(URL));
                }
        );
    }

    @SneakyThrows
    public static void deleteDirectoryRecursively(Path directory) {
        if (Files.notExists(directory))
            return;

        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private String generateFileCaption(ApplicationRequest request, User user) {
        return String.format(
                """
                         #%s #TASK_1 #TASK_2
                                \s
                         Student name - %s
                         Exam time - %s
                        \s""",
                request.getCenter(),
                user.getFirstName(),
                request.getExamDate()
        );
    }

    private void deleteFile(String url) {
        Path path = Paths.get(url);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @SneakyThrows(IOException.class)
    public String generateTxtFileStudent(ApplicationRequest request) {
        User user = userService.getByChatId(request.getChatId());

        Path base = Paths.get(BASE_FILE_URL);
        if (!Files.exists(base))
            Files.createDirectories(base);


        String fileName = user.getFirstName() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm")) + "_file" + ".txt";

        String innerInfo = String.format(
                """
                         Student info:
                             Id: %s
                             Name: %s
                             Phone number: %s
                             Surname: %s
                             Username: %s
                             Role: STUDENT
                             Sent date: %s
                                                \s
                         Exam info:
                             Exam center: '%s'
                             Exam date: '%s'
                             Exam tasks: Task 1 and Task 2
                                                \s
                         Task 1
                             Question: '%s'
                             Size of files for task 1: %s
                                                \s
                         Task 2
                             Question: '%s'
                        \s""", user.getId(), user.getFirstName(), user.getPhoneNumber(),
                user.getLastName() == null ? "None" : user.getLastName(),
                user.getUsername() == null ? "None" : user.getUsername(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                request.getCenter(),
                request.getExamDate().formatted(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                request.getTask1Question(),
                request.getAttachmentsUrlsForTask1().size(),
                request.getTask2Question()
        );

        String absoluteUrl = BASE_FILE_URL + "\\" + fileName;
        Path path = Paths.get(absoluteUrl);

        Files.createDirectories(path.getParent());
        Files.writeString(path, innerInfo);

        return absoluteUrl;
    }

    public static void zipDirectory(String sourceDir, String outputZipFile) throws IOException {
        File dir = new File(sourceDir);
        if (!dir.exists() || !dir.isDirectory())
            throw new IllegalArgumentException("The provided path is not a valid directory.");

        try (FileOutputStream fos = new FileOutputStream(outputZipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            zipFilesRecursively(dir, dir.getName(), zos);
        }
    }

    private static void zipFilesRecursively(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden())
            return;

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zos.putNextEntry(new ZipEntry(fileName));
                zos.closeEntry();
            } else {
                zos.putNextEntry(new ZipEntry(fileName + "/"));
                zos.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFilesRecursively(childFile, fileName + "/" + childFile.getName(), zos);
                }
            }
            return;
        }

        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
        }
    }
}