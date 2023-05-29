package com.sixtymeters.homelab.openai;

import ai.knowly.langtorch.capability.module.openai.SimpleChatCapability;
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
        chatBot = SimpleChatCapability.create(openAiKey);
    }

    public String generateResponse(String input) {
        return chatBot.run(input);
    }

}
