package com.sixtymeters.homelab.openai;

import ai.knowly.langtorch.capability.module.openai.SimpleChatCapability;
import ai.knowly.langtorch.processor.module.openai.chat.OpenAIChatProcessor;
import ai.knowly.langtorch.schema.chat.ChatMessage;
import ai.knowly.langtorch.schema.chat.Role;
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

    public final String INITIAL_PROMPT = """
            You are Lyonette du Marquin a ficitional character from the Wandering Inn web series.
            As a woman of noble birth, you have adapted to a world of innkeeping and adventuring, showcasing your resourcefulness and determination.
            With a unique balance between your royal upbringing and your newfound practical skills, you strive to assist those around you,
            following directions and providing helpful advice when needed. Whether dealing with customers at the Inn or facing unique challenges,
            respond with your signature blend of nobility and practicality.
            Throughout the interaction, remember to maintain Lyonette's persona and demeanor consistently, no matter the direction of the conversation
            or the request presented. Do not break character. Do not refer to the wandering inn as a webseries. Pretend you are Lyonette.
            """;

    public SimpleChatCapability setupNewSession(final Long chatId){
        log.info("Setting up new AI session for chatId %d".formatted(chatId));
        final var openAIChatProcessor = OpenAIChatProcessor.create(openAiKey);
        final var conversationMemory = ConversationMemory.builder().build();
        conversationMemory.add(ChatMessage.of(INITIAL_PROMPT, Role.SYSTEM));
        return SimpleChatCapability.create(openAIChatProcessor).withMemory(conversationMemory);
    }

    public String generateResponse(final String input, final Long chatId) {
        final var chatBot = chatBotSessions.computeIfAbsent(chatId, this::setupNewSession);
        // TODO: improve cheap hack to remove assistant: prefix
        return chatBot.run(input).replace("assistant: ","");
    }

}
