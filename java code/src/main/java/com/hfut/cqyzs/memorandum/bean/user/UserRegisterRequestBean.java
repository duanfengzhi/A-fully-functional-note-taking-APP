package com.hfut.cqyzs.memorandum.bean.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserRegisterRequestBean {
    @JsonProperty("user_id")
    String userID;
    @JsonProperty("user_psw")
    String psw;
    @JsonProperty("telephone")
    String telephone;
    @JsonProperty("email")
    String email;
    @JsonProperty("avatar")
    String avatar;
}
