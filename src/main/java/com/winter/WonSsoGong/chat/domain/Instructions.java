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
public class Instructions {

    @JsonProperty("keywords")
    public String keywords;

    @JsonProperty("title")
    public String title;

    @JsonProperty("description")
    public String description;

}
