package uz.ieltszone.writequestionsbot.service.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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
        document.setCaption(generateFileCaption(request, user));

        execute(document);

        CompletableFuture.runAsync(
                () -> deleteFile(url)
        );
    }

    @SneakyThrows
    public void sendApplicationToGroupWithPhotos(ApplicationRequest request) {
        User user = userService.getByChatId(request.getStudentChatId());
        String URL = BASE_FILE_URL + "\\" + System.currentTimeMillis() + "_" + UUID.randomUUID();
        Path target = Paths.get(URL);

        if (!Files.exists(target)) {
            Files.createDirectory(target);
        }

        String textFileURL = generateTxtFileStudent(request);
        Path sourcePath = Paths.get(textFileURL);

        Path targetPath = target.resolve(sourcePath.getFileName());

        System.out.println("Moving text file from: " + sourcePath + " to " + targetPath);
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        for (Long attachmentId : request.getAttachments()) {
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new RuntimeException("Attachment not found"));

            Path attachmentSourcePath = Paths.get(attachment.getFilePath());
            Path attachmentTargetPath = target.resolve(Paths.get(attachment.getFileName()));

            System.out.println("Moving attachment from: " + attachmentSourcePath + " to " + attachmentTargetPath);
            Files.move(attachmentSourcePath, attachmentTargetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        String outputZipFile = URL + ".zip";
        zipDirectory(URL, outputZipFile);

        SendDocument document = new SendDocument();
        document.setCaption(generateFileCaption(request, user));
        document.setChatId(GROUP_ID);
        document.setParseMode(ParseMode.MARKDOWN);
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
        if (Files.notExists(directory)) {
            System.out.println("Directory does not exist: " + directory);
            return;
        }

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
        return "New application";
    }

    private String generateFileName(User user) {
        return user.getFirstName() + "-" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        ) + ".txt";
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
        User user = userService.getByChatId(request.getStudentChatId());

        Path base = Paths.get(BASE_FILE_URL);
        if (!Files.exists(base))
            Files.createDirectories(base);

        String fileName = System.currentTimeMillis() + "_" + request.getStudentChatId() + ".txt";

        StringBuilder sb = new StringBuilder();

        sb.append(String.format(
                        """
                                 Student's name and surname - %s %s
                                 Submitted time - %s
                                                                \s
                                 Student's exam place - %s
                                 Type of task - %s
                                                                \s
                                 Student's question - '%s'
                                \s""", user.getFirstName(), user.getLastName() == null ? "" :
                                user.getLastName(), request.getWhenTime(), request.getLearningCenter(),
                        request.getTask(), request.getQuestionAsText()
                )
        );

        String absoluteUrl = BASE_FILE_URL + "\\" + fileName;
        Path path = Paths.get(absoluteUrl);

        Files.createDirectories(path.getParent());
        Files.write(path, sb.toString().getBytes());

        return absoluteUrl;
    }

    public static void zipDirectory(String sourceDir, String outputZipFile) throws IOException {
        File dir = new File(sourceDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("The provided path is not a valid directory.");
        }

        try (FileOutputStream fos = new FileOutputStream(outputZipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            zipFilesRecursively(dir, dir.getName(), zos);
        }
    }

    private static void zipFilesRecursively(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

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