package com.winter.WonSsoGong.chat.controller;

import com.winter.WonSsoGong.chat.dto.ChatGptResponse;
import com.winter.WonSsoGong.chat.dto.ContentRequest;
import com.winter.WonSsoGong.chat.service.CustomChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CustomChatBotController {

    private final CustomChatBotService customChatBotService;

    @PostMapping("/bot")
    public ResponseEntity<ChatGptResponse> getChatGptResponse(@RequestBody ContentRequest contentRequest) {
        ChatGptResponse chatGptResponse = customChatBotService.getChatGptResponse(contentRequest);

        return ResponseEntity.ok().body(chatGptResponse);
    }
}
