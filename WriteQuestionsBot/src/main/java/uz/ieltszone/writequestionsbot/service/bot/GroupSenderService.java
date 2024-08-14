package uz.ieltszone.writequestionsbot.service.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import uz.ieltszone.writequestionsbot.config.BotConfiguration;

@Slf4j
@Component
public class GroupSenderService extends DefaultAbsSender {
    private final BotConfiguration botConfiguration;

    protected GroupSenderService(BotConfiguration botConfiguration) {
        super(new DefaultBotOptions());
        this.botConfiguration = botConfiguration;
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getBotToken();
    }
}