package com.winter.WonSsoGong.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.winter.WonSsoGong.chat.domain.Choice;
import com.winter.WonSsoGong.chat.domain.Usage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGptResponse {

    @JsonProperty("id")
    public String id;

    @JsonProperty("object")
    public String object;

    @JsonProperty("created")
    public Integer created;

    @JsonProperty("model")
    public String model;

    @JsonProperty("choices")
    public List<Choice> choices;

    @JsonProperty("usage")
    public Usage usage;

    @JsonProperty("system_fingerprint")
    public Object systemFingerprint;

}
