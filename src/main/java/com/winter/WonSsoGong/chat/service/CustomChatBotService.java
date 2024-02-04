package com.winter.WonSsoGong.chat.service;

import com.winter.WonSsoGong.chat.domain.Message;
import com.winter.WonSsoGong.chat.dto.ChatGptRequest;
import com.winter.WonSsoGong.chat.dto.ChatGptResponse;
import com.winter.WonSsoGong.chat.dto.CreateBillRequest;
import com.winter.WonSsoGong.chat.dto.DebateRequest;
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

    public ChatGptResponse createBill(CreateBillRequest createBillRequest) {

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
                "Keywords: " + createBillRequest.getKeyWord() + "\n" +
                        "Title: " + createBillRequest.getTitle() + "\n" +
                        "Description: " + createBillRequest.getContent()
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

        return template.postForObject(chatGptUrl, request, ChatGptResponse.class);
    }

    public ChatGptResponse debate(DebateRequest debateRequest) {
        List<Message> messageList = new ArrayList<>();

        String instruction = "Title: 국민의 생명과 재산을 보호하기 위한 재난 관리 통합 법률안 제정\\nRationale:\\n최근 증가하는 자연 재난 및 인공 재난에 대비하여 국민의 생명과 재산을 보호하고, 재난 발생 시 신속하고 효과적인 대응을 위한 법적 기반을 마련하는 것이 필요하다는 판단하에 제안하게 되었습니다. 재난에 대한 충분한 예방 조치와 신속한 대응은 국민 안전을 보장하고 사회 안정을 유지하기 위한 필수적인 조건입니다.\\nContent:\\n1. 재난 관리 통합 법률의 제정\\n- 재난 관리 통합 법률을 제정하여 재난에 대한 예방, 대응, 복구에 관한 체계적인 규제를 마련합니다.\\n- 행정안전부장관이 재난 발생 시 특별히 어려움을 겪을 수 있는 지역 또는 취약계층의 안전을 보호하기 위한 제도를 도입합니다.\\n\\n2. 재난에 대한 감사 및 평가\\n- 정부는 재난에 대한 감사 및 평가를 국민과 투명하게 공개합니다.\\n- 재난 관련 교육 및 안전관리에 관한 조치에 대한 국민 참여를 적극적으로 유도합니다.\\n\\n3. 재난 관련 교육의 강화\\n- 교육부는 학교 교육과정에 재난 관리 교육을 의무화하고, 국민대표회의를 통해 국민전체에 대한 재난 관련 교육을 실시합니다.\\n\\n4. 벌금 및 형벌 조항\\n- 재난 발생 시 피해를 입힌 주체에 대한 벌금 및 형벌 조항을 마련하여 범죄 예방 및 국민의 생명과 재산을 보호합니다.\\n\\n5. 유관기관 간 협력체계 강화\\n- 국가기관 및 지방자치단체 간 재난 관리에 관한 협력체계 강화를 위한 조치를 마련합니다." +
                "\"As a Member of Parliament, you're going to dive into a discussion about this legislative bill. Listen, it's important to not just skim the surface. We need a deep, meaningful conversation here. Your discussion should be based on the content provided as well as what the user chimes in with during the chat. Don't forget, the goal here is to really understand what's going on, address any issues, and try to find some common ground. So, get into the weeds, address the details, and don't be afraid to ruffle some feathers if needed.\"";
        
        Message systemMessage = new Message(
                system,
                instruction
        );
        messageList.add(systemMessage);

        Message userMessage = new Message(
                user,
                debateRequest.getContent()
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

        return template.postForObject(chatGptUrl, request, ChatGptResponse.class);
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

        return content.toString();
    }

}
