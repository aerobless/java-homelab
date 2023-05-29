package com.sixtymeters.homelab.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Service
public class ChatService {

    @Value("${homelab.chat.bot-token}")
    private String botToken;

    @EventListener(ApplicationReadyEvent.class)
    public void setupBots() {
        log.info("Setting up Telegram bots");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new HelloWorldBot(botToken));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
