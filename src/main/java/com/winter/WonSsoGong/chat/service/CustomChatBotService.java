package com.winter.WonSsoGong.chat.service;

import com.winter.WonSsoGong.chat.domain.Context;
import com.winter.WonSsoGong.chat.domain.Instructions;
import com.winter.WonSsoGong.chat.domain.Message;
import com.winter.WonSsoGong.chat.dto.ChatGptRequest;
import com.winter.WonSsoGong.chat.dto.ChatGptResponse;
import com.winter.WonSsoGong.chat.dto.ContentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomChatBotService {

    private static final String system = "system";
    private static final String user = "user";
    private final RestTemplate template;
    @Value("${chatgpt.api.url}")
    private String chatGptUrl;

    @Value("${chatgpt.model}")
    private String chatGptModel;

    public ChatGptResponse getChatGptResponse(ContentRequest contentRequest) {

        List<Message> messageList = new ArrayList<>();

        String instruction = readInstruction();
//        final String instruction = "안녕하세요, 법률 작성 전문 도우미 AI '빵긋'입니다. 법안 제안을 위해 다음 정보를 제공해주세요: 1. 필요한 법안에 대한 주요 키워드를 알려주세요. 2. 법안의 핵심을 담은 간결한 제목을 입력해주세요. 3. 법안의 주요 내용과 필요성에 대한 설명을 제공해주세요. 이 정보를 바탕으로, 법안 제안을 다음 세 가지 섹션으로 구성해드릴 것입니다: 1. 제목: 법안의 본질을 정확하게 대표하는 간결하고 설득력 있는 제목입니다. 2. 근거: 법안의 필요성과 중요성에 대한 타당한 설명입니다. 이는 법안의 정당성, 이점, 그리고 잠재적 영향을 개요화하는 부분입니다. 3. 내용: 법안의 주요 규정, 조항, 세부사항을 개요화하는 체계적인 섹션입니다. 이 내용은 명확하고 일관되며 법안의 목표와 범위와 일치해야 합니다. 법률 작성 관례를 준수하며 적절한 법률용어와 구조를 사용하겠습니다. 또한, 대상자, 법률 선례, 그리고 적용 가능한 법률을 고려하여 작성하겠습니다. 추가적인 안내나 지원이 필요하시면 언제든지 요청해주세요. 함께 효과적인 법안을 만들어 봅시다. 모든 텍스트는 한국어로 작성해주세요.";

        Message systemMessage = new Message(
                system,
                instruction
        );
        messageList.add(systemMessage);

        Message userMessage = new Message(
                user,
                "키워드: " + contentRequest.getKeyWord() + "\n" +
                        "제목: " + contentRequest.getTitle() + "\n" +
                        "설명: " + contentRequest.getContent()
        );
        messageList.add(userMessage);

        ChatGptRequest request = new ChatGptRequest(
                chatGptModel,
                messageList,
                1,
                2000,
                1,
                0,
                0
        );

        return template.postForObject(chatGptUrl, request, ChatGptResponse.class);
    }

    private String readInstruction() {
        String filePath = "DraftingLegislation.txt";
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
