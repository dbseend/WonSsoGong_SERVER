package com.winter.WonSsoGong.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {

    @Value("${openai.key}")
    private String openAiKey;

    @Bean
    public RestTemplate template(){
        RestTemplate template = new RestTemplate();
        template.getInterceptors().add(((request, body, execution) -> {
            request.getHeaders().add("Authorization","Bearer "+openAiKey);
            return execution.execute(request,body);
        }));
        return template;
    }
}