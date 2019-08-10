package com.hfut.cqyzs.memorandum.bean.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Item {
    @JsonProperty("itemId")
    int item_id;
    @JsonProperty("userId")
    String user_id;
    @JsonProperty("name")
    String name;
    @JsonProperty("isTrash")
    int is_trash;
    @JsonProperty("status")
    int status;
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    String create_time;
}
