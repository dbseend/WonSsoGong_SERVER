package com.winter.WonSsoGong.chat.controller;

import com.winter.WonSsoGong.chat.dto.ChatGptResponse;
import com.winter.WonSsoGong.chat.dto.CreateBillRequest;
import com.winter.WonSsoGong.chat.service.CustomChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CustomChatBotController {

    private final CustomChatBotService customChatBotService;

    @PostMapping("/bot")
    public ResponseEntity<String> createBill(@RequestBody CreateBillRequest createBillRequest) {
        String chatGptResponse = customChatBotService.createBill(createBillRequest);

        return ResponseEntity.ok().body(chatGptResponse);
    }
}
