package com.sixtymeters.homelab.openai;

import ai.knowly.langtorch.capability.module.openai.SimpleChatCapability;
import ai.knowly.langtorch.processor.module.openai.chat.OpenAIChatProcessor;
import ai.knowly.langtorch.store.memory.conversation.ConversationMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AiCompletionService {

    @Value("${homelab.openapi.key}")
    private String openAiKey;

    private final Map<Long, SimpleChatCapability> chatBotSessions = new ConcurrentHashMap<>();

    public SimpleChatCapability setupNewSession(final Long chatId){
        log.info("Setting up new AI session for chatId %d".formatted(chatId));
        final var openAIChatProcessor = OpenAIChatProcessor.create(openAiKey);
        final var conversationMemory = ConversationMemory.builder().build();
        return SimpleChatCapability.create(openAIChatProcessor).withMemory(conversationMemory).withVerboseMode();
    }

    public String generateResponse(final String input, final Long chatId) {
        final var chatBot = chatBotSessions.computeIfAbsent(chatId, this::setupNewSession);
        return chatBot.run(input);
    }

}
