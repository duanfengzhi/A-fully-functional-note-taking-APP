package com.hfut.cqyzs.memorandum.bean.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserResetPswRequestBean {
    @JsonProperty("user_id")
    String user_id;
    @JsonProperty("user_psw")
    String user_psw;
}
