package shaun.sweethome.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class TelegramService {

    private final RestClient restClient = RestClient.create();

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.chat-id}")
    private String chatId;

    public void sendMessage(String text) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        restClient.post()
                .uri(url)
                .body(Map.of(
                        "chat_id", chatId,
                        "text", text
                ))
                .retrieve()
                .body(String.class);
    }
}
