package com.winter.WonSsoGong.chat.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Choice {

    @JsonProperty("index")
    public Integer index;

    @JsonProperty("message")
    public Message message;

    @JsonProperty("logprobs")
    public Object logprobs;

    @JsonProperty("finish_reason")
    public String finishReason;

}
