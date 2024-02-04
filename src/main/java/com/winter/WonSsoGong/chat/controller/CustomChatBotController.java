package com.winter.WonSsoGong.chat.controller;

import com.winter.WonSsoGong.chat.dto.ChatGptResponse;
import com.winter.WonSsoGong.chat.dto.CreateBillRequest;
import com.winter.WonSsoGong.chat.dto.DebateRequest;
import com.winter.WonSsoGong.chat.service.CustomChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/chat")
public class CustomChatBotController {

    private final CustomChatBotService customChatBotService;

    @PostMapping("/bill")
    public ResponseEntity<ChatGptResponse> createBill(@RequestBody CreateBillRequest createBillRequest) {
        ChatGptResponse chatGptResponse = customChatBotService.createBill(createBillRequest);

        return ResponseEntity.ok().body(chatGptResponse);
    }

    @PostMapping("/debate")
    public ResponseEntity<ChatGptResponse> debate(@RequestBody DebateRequest debateRequest) {
        ChatGptResponse chatGptResponse = customChatBotService.debate(debateRequest);

        return ResponseEntity.ok().body(chatGptResponse);
    }
}
