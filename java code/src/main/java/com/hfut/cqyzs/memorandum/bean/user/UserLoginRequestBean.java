package com.hfut.cqyzs.memorandum.bean.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserLoginRequestBean {
    @JsonProperty("user_id")
    String user_id;
    @JsonProperty("user_psw")
    String user_psw;
}
