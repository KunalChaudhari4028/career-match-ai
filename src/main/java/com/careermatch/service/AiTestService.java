
package com.careermatch.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiTestService {

    private final ChatClient chatClient;

    public AiTestService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String testOpenAi() {

        String response = chatClient.prompt()
                .user("Explain Spring Boot in two simple sentences.")
                .call()
                .content();

        return response;
    }
}
