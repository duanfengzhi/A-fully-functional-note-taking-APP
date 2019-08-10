package com.hfut.cqyzs.memorandum.bean.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class UserAccountRequestBean {
    @JsonProperty("user_id")
    String user_id;
    @JsonProperty("telephone")
    String telephone;
    @JsonProperty("email")
    String email;
}
