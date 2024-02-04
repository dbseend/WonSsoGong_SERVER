package com.winter.WonSsoGong.chat.service;

import com.winter.WonSsoGong.chat.domain.Message;
import com.winter.WonSsoGong.chat.dto.ChatGptRequest;
import com.winter.WonSsoGong.chat.dto.ChatGptResponse;
import com.winter.WonSsoGong.chat.dto.CreateBillRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomChatBotService {

    private static final String system = "system";
    private static final String user = "user";
    private static final Integer temperature = 1;
    private static final Integer maxTokens = 2000;
    private static final Integer topP = 1;
    private static final Integer frequencyPenalty = 0;
    private static final Integer presencePenalty = 0;
    private final RestTemplate template;
    @Value("${chatgpt.api.url}")
    private String chatGptUrl;
    @Value("${chatgpt.model}")
    private String chatGptModel;

    public String createBill(CreateBillRequest createBillRequest) {

        List<Message> messageList = new ArrayList<>();

        String createBillInstruction = "DraftingLegislation.txt";
        String instruction = readInstruction(createBillInstruction);

        Message systemMessage = new Message(
                system,
                instruction
        );
        messageList.add(systemMessage);

        Message userMessage = new Message(
                user,
                "키워드: " + createBillRequest.getKeyWord() + "\n" +
                        "제목: " + createBillRequest.getTitle() + "\n" +
                        "설명: " + createBillRequest.getContent()
        );
        messageList.add(userMessage);

        ChatGptRequest request = new ChatGptRequest(
                chatGptModel,
                messageList,
                temperature,
                maxTokens,
                topP,
                frequencyPenalty,
                presencePenalty
        );

        ChatGptResponse chatGptResponse = template.postForObject(chatGptUrl, request, ChatGptResponse.class);
        String response = chatGptResponse.getChoices().get(0).getMessage().getContent();

        log.info(chatGptResponse.getChoices().get(0).getMessage().getContent());

        return response;
    }

    private String readInstruction(String filePath) {

        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info(String.valueOf(content));

        return content.toString();
    }

}
