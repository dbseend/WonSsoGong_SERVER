package com.winter.WonSsoGong.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.winter.WonSsoGong.chat.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatGptRequest {

    @JsonProperty("model")
    public String model;

    @JsonProperty("messages")
    public List<Message> messages;

    @JsonProperty("temperature")
    public Integer temperature;

    @JsonProperty("max_tokens")
    public Integer maxTokens;

    @JsonProperty("top_p")
    public Integer topP;

    @JsonProperty("frequency_penalty")
    public Integer frequencyPenalty;

    @JsonProperty("presence_penalty")
    public Integer presencePenalty;

}