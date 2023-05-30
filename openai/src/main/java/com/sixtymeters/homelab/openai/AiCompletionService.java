package com.sixtymeters.homelab.openai;

import ai.knowly.langtorch.capability.module.openai.SimpleChatCapability;
import ai.knowly.langtorch.processor.module.openai.chat.OpenAIChatProcessor;
import ai.knowly.langtorch.store.memory.conversation.ConversationMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiCompletionService {

    @Value("${homelab.openapi.key}")
    private String openAiKey;

    SimpleChatCapability chatBot;

    @EventListener(ApplicationReadyEvent.class)
    public void setupAi(){
        log.info("Setting up AI");
        final var openAIChatProcessor = OpenAIChatProcessor.create(openAiKey);
        final var conversationMemory = ConversationMemory.builder().build();
        chatBot = SimpleChatCapability.create(openAIChatProcessor).withMemory(conversationMemory).withVerboseMode();
    }

    public String generateResponse(final String input) {
        return chatBot.run(input);
    }

}
