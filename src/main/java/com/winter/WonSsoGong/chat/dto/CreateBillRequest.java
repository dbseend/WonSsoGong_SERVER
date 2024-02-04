package com.winter.WonSsoGong.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateBillRequest {

    private String keyWord;

    private String title;

    private String content;

}
