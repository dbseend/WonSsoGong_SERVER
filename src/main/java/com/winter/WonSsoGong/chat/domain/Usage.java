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
public class Usage {

    @JsonProperty("prompt_tokens")
    public Integer promptTokens;

    @JsonProperty("completion_tokens")
    public Integer completionTokens;

    @JsonProperty("total_tokens")
    public Integer totalTokens;

}
