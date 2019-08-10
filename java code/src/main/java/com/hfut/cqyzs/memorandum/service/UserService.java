package com.hfut.cqyzs.memorandum.service;

import com.hfut.cqyzs.memorandum.bean.content.Content;
import com.hfut.cqyzs.memorandum.bean.content.ContentResponse;
import com.hfut.cqyzs.memorandum.bean.item.Item;
import com.hfut.cqyzs.memorandum.bean.item.ItemResponse;
import com.hfut.cqyzs.memorandum.bean.record.Record;
import com.hfut.cqyzs.memorandum.bean.record.RecordResponse;
import com.hfut.cqyzs.memorandum.bean.task.Task;
import com.hfut.cqyzs.memorandum.bean.task.TaskResponse;
import com.hfut.cqyzs.memorandum.bean.user.*;

import java.util.List;

public interface UserService {

    UserAccountResponseBean userAccount(UserAccountRequestBean requestBean);

    UserLoginResponseBean userLogin(UserLoginRequestBean requestBean);

    UserRegisterResponseBean userRegister(UserRegisterRequestBean userRegisterRequestBean);

    UserResetPswResponseBean userResetPsw(UserResetPswRequestBean requestBean);

    RecordResponse recordInsert(Record record);

    ItemResponse itemInsert(Item item);

    TaskResponse taskInsert(Task task);

    ContentResponse contentInsert(Content content);

    List<Record> recordSelect(String userId);

    List<Item> itemSelect(String userId);

    List<Task> taskSelect(String userId);

    List<Content> contentSelect(int record_id);

    RecordResponse recordUpdate(Record record);

    ItemResponse itemUpdate(Item item);

    TaskResponse taskUpdate(Task task);

    ContentResponse contentUpdate(Content content);

    String recordSearchItemTime(int itemId);
    String taskSearchItemTime(int itemId);

    List<Integer> recordIdSelect(String user_id);
    String timeSelect(int record_id);

    ItemResponse itemDelete(String user_id, String create_time);
    TaskResponse taskDelete(String user_id, String create_time);
    RecordResponse recordDelete(String user_id, String create_time);
    ContentResponse contentDelete(int record_id);

    int timeSearchItemId(String create_time);
    int timeSearchRecordId(String create_time);

    int recordIdDeleteSelect(String user_id, String create_time);

    UserRegisterResponseBean userIdIsHave(String user_id);
    UserRegisterResponseBean telephoneIsRegister(String telephone);

    String getTelephoneByUserId(String user_id);
}
