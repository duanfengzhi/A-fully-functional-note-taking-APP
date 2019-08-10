package com.hfut.cqyzs.memorandum.controller;

import com.hfut.cqyzs.memorandum.bean.DeleteAll;
import com.hfut.cqyzs.memorandum.bean.DeleteAllRequest;
import com.hfut.cqyzs.memorandum.bean.content.*;
import com.hfut.cqyzs.memorandum.service.UserService;
import com.hfut.cqyzs.memorandum.utils.Message;
import com.hfut.cqyzs.memorandum.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Message contentInsert(@RequestBody @Valid ContentAddTimeList contents) {
        for (int i = 0; i < contents.getContents().size(); i++) {
            try {
                Content content = contents.getContents().get(i).getContent();
                String time = contents.getContents().get(i).getTime();
                int recordId = userService.timeSearchRecordId(time);
                content.setRecord_id(recordId);
                ContentResponse result = userService.contentInsert(content);
                if(result.getStatus() == 0){
                    return ResultUtil.error("0001","数据库执行错误");
                }
                if(i == contents.getContents().size() - 1) {
                    return ResultUtil.success(result);
                }
            } catch (Exception e) {
                return ResultUtil.error("0001", e.getMessage());
            }
        }
        return ResultUtil.error("0001","错误");
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public Message contentSelect(@RequestParam("userId")String userId) {
        try{
            List<Integer> recordId = userService.recordIdSelect(userId);
            List<ContentAddRecordTime> list = new ArrayList<>();
            for(int i = 0;i < recordId.size();i++){
                List<Content> contents = userService.contentSelect(recordId.get(i));
                String time = userService.timeSelect(recordId.get(i));
                for(int j = 0;j < contents.size();j++) {
                    ContentAddRecordTime contentAddRecordTime = new ContentAddRecordTime();
                    Content content = contents.get(j);
                    contentAddRecordTime.setContent_id(content.getContent_id());
                    contentAddRecordTime.setRecord_id(content.getRecord_id());
                    contentAddRecordTime.setRecord_content(content.getRecord_content());
                    contentAddRecordTime.setContent_type(content.getContent_type());
                    contentAddRecordTime.setContent_order(content.getContent_order());
                    contentAddRecordTime.setRecordCreateTime(time);
                    list.add(contentAddRecordTime);
                }

            }
            return ResultUtil.success(list);
        }catch (Exception e) {
            return ResultUtil.error("0001",e.getMessage());
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Message contentDelete(@RequestBody @Valid DeleteAllRequest deleteAllList){
        try{
            for(int i = 0;i < deleteAllList.getDeleteAllList().size();i++) {
                DeleteAll deleteAll = deleteAllList.getDeleteAllList().get(i);
                String userId = deleteAll.getUser_id();
                String createTime = deleteAll.getCreate_time();
                int recordId = userService.recordIdDeleteSelect(userId, createTime);
                ContentResponse result = userService.contentDelete(recordId);
                if(result.getStatus()==0){
                    return ResultUtil.error("0001", "执行错误");
                }
                if(i == deleteAllList.getDeleteAllList().size() - 1) {
                    return ResultUtil.success(result);
                }
            }
        }catch (Exception e) {
            return ResultUtil.error("0001",e.getMessage());
        }
        return ResultUtil.error("0001", "错误");
    }

}
