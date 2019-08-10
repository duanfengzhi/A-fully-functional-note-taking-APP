package com.hfut.cqyzs.memorandum.controller;

import com.hfut.cqyzs.memorandum.bean.DeleteAll;
import com.hfut.cqyzs.memorandum.bean.DeleteAllRequest;
import com.hfut.cqyzs.memorandum.bean.task.*;
import com.hfut.cqyzs.memorandum.service.UserService;
import com.hfut.cqyzs.memorandum.utils.Message;
import com.hfut.cqyzs.memorandum.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Message recordInsert(@RequestBody @Valid TaskAddTimeList tasks) {
        for (int i = 0; i < tasks.getTasks().size(); i++) {
            try {
                Task task = tasks.getTasks().get(i).getTask();
                String time = tasks.getTasks().get(i).getTime();
                int itemID = userService.timeSearchItemId(time);
                task.setItem_id(itemID);
                TaskResponse result = userService.taskInsert(task);
                if(result.getStatus() == 0){
                    return ResultUtil.error("0001","数据库执行错误");
                }
                if(i == tasks.getTasks().size() - 1) {
                    return ResultUtil.success(result);
                }
            } catch (Exception e) {
                return ResultUtil.error("0001", e.getMessage());
            }
        }
        return ResultUtil.error("0001","错误");
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public Message taskSelect(@RequestParam("userId")String userId) {
        List<TaskAddTime> taskAddTimeList = new ArrayList();
        try{
            List<Task> tasks = userService.taskSelect(userId);
            for(int i = 0;i < tasks.size();i++) {
                TaskAddTime taskAddTime = new TaskAddTime();
                String itemTime = userService.taskSearchItemTime(tasks.get(i).getItem_id());
                taskAddTime.setTask(tasks.get(i));
                taskAddTime.setTime(itemTime);
                try{
                    taskAddTimeList.add(taskAddTime);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ResultUtil.success(taskAddTimeList);
        }catch (Exception e) {
            return ResultUtil.error("0001",e.getMessage());
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Message taskUpdate(@RequestBody @Valid TaskAddTimeList tasks) {

        for (int i = 0; i < tasks.getTasks().size(); i++) {
            try {
                Task task = tasks.getTasks().get(i).getTask();
                String time = tasks.getTasks().get(i).getTime();
                int itemID = userService.timeSearchItemId(time);
                task.setItem_id(itemID);
                TaskResponse result = userService.taskUpdate(task);
                if(result.getStatus() == 0){
                    return ResultUtil.error("0001", "执行错误");
                }
                if ((i == tasks.getTasks().size() - 1)) {
                    return ResultUtil.success(result);
                }
            } catch (Exception e) {
                return ResultUtil.error("0001", e.getMessage());
            }
        }
        return ResultUtil.error("0001", "错误");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Message taskDelete(@RequestBody @Valid DeleteAllRequest deleteAllList){
        try{
            for(int i = 0;i < deleteAllList.getDeleteAllList().size();i++) {
                DeleteAll deleteAll = deleteAllList.getDeleteAllList().get(i);
                String userId = deleteAll.getUser_id();
                String createTime = deleteAll.getCreate_time();
                TaskResponse result = userService.taskDelete(userId, createTime);
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
