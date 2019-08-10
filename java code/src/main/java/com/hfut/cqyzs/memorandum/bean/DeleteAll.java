package com.hfut.cqyzs.memorandum.bean;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeleteAll {
    @JsonProperty("userId")
    String user_id;
    @JsonProperty("createTime")
    String create_time;
}
