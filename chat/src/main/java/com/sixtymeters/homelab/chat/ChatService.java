package com.sixtymeters.homelab.chat;

import com.sixtymeters.homelab.openai.AiCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    @Value("${homelab.chat.bot-token}")
    private String botToken;

    @Value("${homelab.chat.allowed-chat-ids}")
    private List<Long> allowedChatIds;

    @Value("${homelab.chat.creator-chat-id}")
    private Long creatorChatId;

    private final AiCompletionService completionService;

    @EventListener(ApplicationReadyEvent.class)
    public void setupBots() {
        log.info("Setting up Telegram bots");
        try {
            final var botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new AriyaaBot(botToken, completionService, allowedChatIds, creatorChatId));
        } catch (final TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
