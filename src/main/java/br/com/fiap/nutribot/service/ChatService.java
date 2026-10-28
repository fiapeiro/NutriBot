package br.com.fiap.nutribot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
            .maxMessages(10)
            .build();

    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(), //advisor para logar as mensagens
                        MessageChatMemoryAdvisor.builder(memory).build() //advisor para manter o histórico de mensagens
                )
                .build();
    }

    public String chat(String conversationId, String message) {
        System.out.println("Conversation ID: " + conversationId);
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId)) // passa o conversationId para o advisor de memória
                .call()
                .content();
    }
}
