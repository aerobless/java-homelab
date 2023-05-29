package com.sixtymeters.homelab.chat;

import com.sixtymeters.homelab.openai.AiCompletionService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
public class AriyaaBot extends TelegramLongPollingBot {
    private final AiCompletionService completionService;
    private final List<Long> allowedChatIds;

    public AriyaaBot(String botToken, AiCompletionService completionService, List<Long> allowedChatIds) {
        super(botToken);
        this.completionService = completionService;
        this.allowedChatIds = allowedChatIds;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();
            String firstName = update.getMessage().getFrom().getFirstName();
            String lastName = update.getMessage().getFrom().getLastName();
            String message = update.getMessage().getText();

            if(!allowedChatIds.contains(chatId)) {
                log.info("User %s (%s %s) with chatId %d sent '%s' but the chatId is not allowed".formatted(username, firstName, lastName, chatId, message));
                return;
            }

            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(completionService.generateResponse(update.getMessage().getText()));

            log.info("%s (%s %s): %s".formatted(username, firstName, lastName, message));
            log.info("Ariyaa: %s".formatted(response.getText()));

            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "Ariyaa";
    }

}
