package com.hfut.cqyzs.memorandum.bean.record;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Record {
    @JsonProperty("recordId")
    int record_id;
    @JsonProperty("userId")
    String user_id;
    @JsonProperty("itemId")
    int item_id;
    @JsonProperty("title")
    String title;
    @JsonProperty("isTrash")
    int is_trash;
    @JsonProperty("isItemTrash")
    int is_item_trash;
    @JsonProperty("createTime")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    String create_time;
    @JsonProperty("background")
    int background;
    @JsonProperty("status")
    int status;
}
