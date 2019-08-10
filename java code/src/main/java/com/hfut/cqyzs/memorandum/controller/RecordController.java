package com.hfut.cqyzs.memorandum.controller;


import com.hfut.cqyzs.memorandum.bean.DeleteAll;
import com.hfut.cqyzs.memorandum.bean.DeleteAllRequest;
import com.hfut.cqyzs.memorandum.bean.record.*;
import com.hfut.cqyzs.memorandum.service.UserService;
import com.hfut.cqyzs.memorandum.utils.Message;
import com.hfut.cqyzs.memorandum.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/record")
public class RecordController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Message recordInsert(@RequestBody @Valid RecordAddTimeList records) {
        for (int i = 0; i < records.getRecords().size(); i++) {
            try {
                Record record = records.getRecords().get(i).getRecord();
                String time = records.getRecords().get(i).getTime();
                int itemID = userService.timeSearchItemId(time);
                record.setItem_id(itemID);
                RecordResponse result = userService.recordInsert(record);
                if(result.getStatus() == 0){
                    return ResultUtil.error("0001","数据库执行错误");
                }
                if(i == records.getRecords().size() - 1) {
                    return ResultUtil.success(result);
                }
            } catch (Exception e) {
                return ResultUtil.error("0001", e.getMessage());
            }
        }
        return ResultUtil.error("0001","错误");
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public Message recordSelect(@RequestParam("userId")String userId) {
        List<RecordAddTime> recordAddTimeList = new ArrayList();
        try{
            List<Record> records = userService.recordSelect(userId);
            for(int i = 0;i < records.size();i++) {
                RecordAddTime recordAddTime = new RecordAddTime();
                String itemTime = userService.recordSearchItemTime(records.get(i).getItem_id());
                recordAddTime.setRecord(records.get(i));
                recordAddTime.setTime(itemTime);
                try{
                    recordAddTimeList.add(recordAddTime);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ResultUtil.success(recordAddTimeList);
        }catch (Exception e) {
            return ResultUtil.error("0001",e.getMessage());
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Message recordUpdate(@RequestBody @Valid RecordAddTimeList records) {

        for (int i = 0; i < records.getRecords().size(); i++) {
            try {
                Record record = records.getRecords().get(i).getRecord();
                String time = records.getRecords().get(i).getTime();
                int itemID = userService.timeSearchItemId(time);
                record.setItem_id(itemID);
                RecordResponse result = userService.recordUpdate(record);
                if(result.getStatus() == 0){
                    return ResultUtil.error("0001", "执行错误");
                }
                if (i == records.getRecords().size() - 1) {
                    return ResultUtil.success(result);
                }
            } catch (Exception e) {
                return ResultUtil.error("0001", e.getMessage());
            }
        }
        return ResultUtil.error("0001", "错误");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Message recordDelete(@RequestBody @Valid DeleteAllRequest deleteAllList){
        try{
            for(int i = 0;i < deleteAllList.getDeleteAllList().size();i++) {
                DeleteAll deleteAll = deleteAllList.getDeleteAllList().get(i);
                String userId = deleteAll.getUser_id();
                String createTime = deleteAll.getCreate_time();
                RecordResponse result = userService.recordDelete(userId, createTime);
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
