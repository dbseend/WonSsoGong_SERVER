package com.winter.WonSsoGong.chat.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Context {

    @JsonProperty("sections")
    public List<String> sections;

    @JsonProperty("additional_conditions")
    public String additionalConditions;
}
