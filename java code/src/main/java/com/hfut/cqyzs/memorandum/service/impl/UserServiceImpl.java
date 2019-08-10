package com.hfut.cqyzs.memorandum.service.impl;

import com.hfut.cqyzs.memorandum.bean.content.Content;
import com.hfut.cqyzs.memorandum.bean.content.ContentResponse;
import com.hfut.cqyzs.memorandum.bean.item.Item;
import com.hfut.cqyzs.memorandum.bean.item.ItemResponse;
import com.hfut.cqyzs.memorandum.bean.record.Record;
import com.hfut.cqyzs.memorandum.bean.record.RecordResponse;
import com.hfut.cqyzs.memorandum.bean.task.Task;
import com.hfut.cqyzs.memorandum.bean.task.TaskResponse;
import com.hfut.cqyzs.memorandum.bean.user.*;
import com.hfut.cqyzs.memorandum.entity.User;
import com.hfut.cqyzs.memorandum.mapper.UserMapper;
import com.hfut.cqyzs.memorandum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class UserServiceImpl implements UserService {
    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserAccountResponseBean userAccount(UserAccountRequestBean userAccountRequestBean) {
        try {
            UserAccountResponseBean result = new UserAccountResponseBean();
            int count = 0;
            if(userAccountRequestBean.getEmail() == null && userAccountRequestBean.getTelephone() != null)
                count = this.userMapper.userTel(userAccountRequestBean.getUser_id(), userAccountRequestBean.getTelephone());
            else if(userAccountRequestBean.getEmail() != null && userAccountRequestBean.getTelephone() == null)
                count = this.userMapper.userEmail(userAccountRequestBean.getUser_id(), userAccountRequestBean.getEmail());

            if(count == 1){ result.setStatus(1); }
            else{ result.setStatus(0); }

            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public UserLoginResponseBean userLogin(UserLoginRequestBean requestBean) {
        try {
            UserLoginResponseBean result = new UserLoginResponseBean();
            User user = this.userMapper.userLogin(requestBean.getUser_id(),requestBean.getUser_psw());
            int count = 0;
            if(user!= null){
                count  = 1;
            }

            if(count == 1){ result.setStatus(1);result.setUser(user); }
            else{ result.setStatus(0); result.setUser(null);}

            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public UserRegisterResponseBean userRegister(UserRegisterRequestBean userRegisterRequestBean) {
        UserRegisterResponseBean userRegisterResponseBean = new UserRegisterResponseBean();
        try {
            int result = this.userMapper.userRegister(userRegisterRequestBean.getUserID(), userRegisterRequestBean.getPsw(),
                    userRegisterRequestBean.getTelephone(), userRegisterRequestBean.getEmail(), userRegisterRequestBean.getAvatar());

            Long time = new Date().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

            if(result == 1){
                this.userMapper.itemInsert(userRegisterRequestBean.getUserID(),"默认",0,10,sdf.format(time));
            }
            userRegisterResponseBean.setStatus(result);
            return userRegisterResponseBean;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public UserResetPswResponseBean userResetPsw(UserResetPswRequestBean requestBean) {
        try {
            UserResetPswResponseBean result = new UserResetPswResponseBean();
            int count = this.userMapper.userResetPsw(requestBean.getUser_id(),requestBean.getUser_psw());
            if(count == 1){ result.setStatus(1); }
            else{ result.setStatus(0); }

            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public RecordResponse recordInsert(Record record) {
        RecordResponse recordResponse = new RecordResponse();
        try {

            int result = this.userMapper.recordInsert(record.getUser_id(),
                    record.getItem_id(), record.getTitle(), record.getIs_trash(),
                    record.getIs_item_trash(), record.getCreate_time(), record.getBackground(),
                    record.getStatus()
            );
            recordResponse.setStatus(result);
            return recordResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ItemResponse itemInsert(Item item) {
        ItemResponse itemResponse = new ItemResponse();
        try{
            int result = this.userMapper.itemInsert(item.getUser_id(), item.getName(),
                    item.getIs_trash(), item.getStatus(), item.getCreate_time());
            itemResponse.setStatus(result);
            return itemResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public TaskResponse taskInsert(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        try{
            int result = this.userMapper.taskInsert(task.getUser_id(), task.getItem_id(), task.getTitle(),
                    task.getPriority(), task.getDescription(), task.getClock(), task.getDeadline(),
                    task.getIs_trash(), task.getIs_item_trash(), task.getState(), task.getCreate_time(), task.getStatus());
            taskResponse.setStatus(result);
            return taskResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ContentResponse contentInsert(Content content) {
        ContentResponse contentResponse = new ContentResponse();
        try{
            int result = this.userMapper.contentInsert(content.getRecord_id(), content.getRecord_content(),
            content.getContent_type(), content.getContent_order());
            contentResponse.setStatus(result);
            return contentResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Record> recordSelect(String userId) {
        try {

            List<Record> result = this.userMapper.recordSelect(userId);
            return result;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Item> itemSelect(String userId) {
        try{
            List<Item> result = this.userMapper.itemSelect(userId);
            return result;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Task> taskSelect(String userId) {
        try{
            List<Task> result = this.userMapper.taskSelect(userId);
            return result;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Content> contentSelect(int record_id) {
        try{
            List<Content> result = this.userMapper.contentSelect(record_id);
            return result;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public RecordResponse recordUpdate(Record record) {
        RecordResponse recordResponse = new RecordResponse();
        try {

            int result = this.userMapper.recordUpdate(record.getUser_id(),
                    record.getItem_id(), record.getTitle(), record.getIs_trash(),
                    record.getIs_item_trash(), record.getCreate_time(), record.getBackground(), record.getStatus()
            );
            recordResponse.setStatus(result);
            return recordResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ItemResponse itemUpdate(Item item) {
        ItemResponse itemResponse = new ItemResponse();
        try{
            int result = this.userMapper.itemUpdate(item.getUser_id(), item.getName(),
                    item.getIs_trash(), item.getStatus(), item.getCreate_time());
            itemResponse.setStatus(result);
            return itemResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public TaskResponse taskUpdate(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        try{
            int result = this.userMapper.taskUpdate(task.getUser_id(), task.getItem_id(), task.getTitle(),
                    task.getPriority(), task.getDescription(), task.getClock(), task.getDeadline(),
                    task.getIs_trash(), task.getIs_item_trash(), task.getState(), task.getCreate_time(), task.getStatus());
            taskResponse.setStatus(result);
            return taskResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ContentResponse contentUpdate(Content content) {
        ContentResponse contentResponse = new ContentResponse();
        try{
            int result = this.userMapper.contentUpdate(content.getRecord_id(), content.getRecord_content(),
                    content.getContent_type(), content.getContent_order());
            contentResponse.setStatus(result);
            return contentResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String recordSearchItemTime(int itemId) {
        try{
            String result = this.userMapper.recordSearchItemTime(itemId);
            return result;
        }catch (Exception e) {
            throw e;
        }
    }
    @Override
    public String taskSearchItemTime(int itemId) {
        try{
            String result = this.userMapper.taskSearchItemTime(itemId);
            return result;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Integer> recordIdSelect(String user_id) {
        try{
            List<Integer> recordId = this.userMapper.recordIdSelect(user_id);
            return recordId;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String timeSelect(int record_id) {
        try{
            String time = this.userMapper.timeSelect(record_id);
            return time;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ItemResponse itemDelete(String user_id, String create_time) {
        ItemResponse itemResponse = new ItemResponse();
        try{
            int result = this.userMapper.itemDelete(user_id, create_time);
            itemResponse.setStatus(result);
            return itemResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public TaskResponse taskDelete(String user_id, String create_time) {
        TaskResponse taskResponse = new TaskResponse();
        try{
            int result = this.userMapper.taskDelete(user_id, create_time);
            taskResponse.setStatus(result);
            return taskResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public RecordResponse recordDelete(String user_id, String create_time) {
        RecordResponse recordResponse = new RecordResponse();
        try{
            int result = this.userMapper.recordDelete(user_id, create_time);
            recordResponse.setStatus(result);
            return recordResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public int timeSearchItemId(String create_time) {
        try{
            int  taskId = this.userMapper.timeSearchItemId(create_time);
            return taskId;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public int timeSearchRecordId(String create_time) {
        try{
            int  recordId = this.userMapper.timeSearchRecordId(create_time);
            return recordId;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public int recordIdDeleteSelect(String user_id, String create_time) {
        try{
            int recordId = this.userMapper.recordIdDeleteSelect(user_id, create_time);
            return recordId;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ContentResponse contentDelete(int record_id) {
        ContentResponse contentResponse = new ContentResponse();
        try{
            int result = this.userMapper.contentDelete(record_id);
            contentResponse.setStatus(result);
            return contentResponse;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public UserRegisterResponseBean userIdIsHave(String user_id){
        UserRegisterResponseBean userRegisterResponseBean = new UserRegisterResponseBean();
        try{
            int result = this.userMapper.userIdIsHave(user_id);
            if(result != 0){
                userRegisterResponseBean.setStatus(2);
                return userRegisterResponseBean;
            }
            userRegisterResponseBean.setStatus(1);
            return userRegisterResponseBean;
        }catch (Exception e) {
            throw e;
        }

    }

    @Override
    public UserRegisterResponseBean telephoneIsRegister(String telephone){
        UserRegisterResponseBean userRegisterResponseBean = new UserRegisterResponseBean();
        try{
            int result = this.userMapper.telephoneIsRegister(telephone);
            if(result != 0){
                userRegisterResponseBean.setStatus(3);
                return userRegisterResponseBean;
            }
            userRegisterResponseBean.setStatus(1);
            return userRegisterResponseBean;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String getTelephoneByUserId(String user_id){
        try{
            String telephone = this.userMapper.getTelephoneByUserId(user_id);

            return telephone;
        }catch (Exception e) {
            throw e;
        }
    }
}
