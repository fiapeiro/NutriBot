package br.com.fiap.nutribot.controller;

import br.com.fiap.nutribot.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    public record ChatRequest(String conversationId, String message){}
    public record ChatResponse(String answer){}

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request){
        log.info("Chegou no controller com a mensagem: {}", request.message());
        return new ChatResponse(chatService.chat(request.conversationId, request.message));
    }
}
