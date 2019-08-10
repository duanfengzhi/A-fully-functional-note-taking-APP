package com.hfut.cqyzs.memorandum.bean.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Task {
    @JsonProperty("taskId")
    int task_id;
    @JsonProperty("userId")
    String user_id;
    @JsonProperty("itemId")
    int item_id;
    @JsonProperty("title")
    String title;
    @JsonProperty("priority")
    int priority;
    @JsonProperty("description")
    String description;
    @JsonProperty("clock")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    String clock;
    @JsonProperty("deadline")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    String deadline;
    @JsonProperty("isTrash")
    int is_trash;
    @JsonProperty("isItemTrash")
    int is_item_trash;
    @JsonProperty("state")
    int state;
    @JsonProperty("createTime")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    String create_time;
    @JsonProperty("status")
    int status;
}
