package com.sixtymeters.homelab.chat;

import com.sixtymeters.homelab.openai.AiCompletionService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
public class AriyaaBot extends AbilityBot {
    private final AiCompletionService completionService;
    private final List<Long> allowedChatIds;
    private final Long creatorChatId;

    public AriyaaBot(final String botToken, final AiCompletionService completionService, final List<Long> allowedChatIds, final Long creatorChatId) {
        super(botToken, "Ariyaa");
        this.completionService = completionService;
        this.allowedChatIds = allowedChatIds;
        this.creatorChatId = creatorChatId;
    }

    @Override
    public long creatorId() {
        return creatorChatId;
    }

    @Override
    public void onUpdateReceived(final Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            final long chatId = update.getMessage().getChatId();
            final var username = update.getMessage().getFrom().getUserName();
            final var firstName = update.getMessage().getFrom().getFirstName();
            final var lastName = update.getMessage().getFrom().getLastName();
            final var message = update.getMessage().getText();

            if(!allowedChatIds.contains(chatId)) {
                log.info("User %s (%s %s) with chatId %d sent '%s' but the chatId is not allowed".formatted(username, firstName, lastName, chatId, message));
                return;
            }

            final var response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(completionService.generateResponse(update.getMessage().getText(), chatId));

            log.info("%s (%s %s): %s".formatted(username, firstName, lastName, message));
            log.info("Ariyaa: %s".formatted(response.getText()));

            try {
                execute(response);
            } catch (final TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
