package shaun.sweethome.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RestController;
import shaun.sweethome.backend.service.TelegramService;

@RestController
@ConditionalOnProperty(name = "telegram.enabled", havingValue = "true")
public class TelegramTestController {

    private final TelegramService telegramService;

    public TelegramTestController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @PostMapping("/api/telegram/test")
    public String test() {
        telegramService.sendMessage("SweetHome 텔레그램 연동 테스트입니다 🌱");
        return "ok";
    }
}
