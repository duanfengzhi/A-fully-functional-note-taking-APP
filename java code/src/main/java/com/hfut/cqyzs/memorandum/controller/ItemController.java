package com.hfut.cqyzs.memorandum.controller;

import com.hfut.cqyzs.memorandum.bean.DeleteAll;
import com.hfut.cqyzs.memorandum.bean.DeleteAllRequest;
import com.hfut.cqyzs.memorandum.bean.item.Item;
import com.hfut.cqyzs.memorandum.bean.item.ItemList;
import com.hfut.cqyzs.memorandum.bean.item.ItemResponse;
import com.hfut.cqyzs.memorandum.service.UserService;
import com.hfut.cqyzs.memorandum.utils.Message;
import com.hfut.cqyzs.memorandum.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Message itemInsert(@RequestBody @Valid ItemList items) {
        for (int i = 0; i < items.getItems().size(); i++) {
            try {
                ItemResponse result = userService.itemInsert(items.getItems().get(i));
                if(result.getStatus() == 0){
                    return ResultUtil.error("0001","数据库执行错误");
                }
                if(i == items.getItems().size() - 1) {
                    return ResultUtil.success(result);
                }
            } catch (Exception e) {
                return ResultUtil.error("0001", e.getMessage());
            }
        }
        return ResultUtil.error("0001","错误");
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public Message itemSelect(@RequestParam("userId")String userId) {

        try{
            List<Item> result = userService.itemSelect(userId);

            return ResultUtil.success(result);
        }catch (Exception e) {
            return ResultUtil.error("0001",e.getMessage());
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Message itemUpdate(@RequestBody @Valid ItemList items) {

        for (int i = 0; i < items.getItems().size(); i++) {
            try {
                ItemResponse result = userService.itemUpdate(items.getItems().get(i));
                if(result.getStatus() == 0){
                    return ResultUtil.error("0001", "执行错误");
                }
                if ((i == items.getItems().size() - 1)) {
                    return ResultUtil.success(result);
                }
            } catch (Exception e) {
                return ResultUtil.error("0001", e.getMessage());
            }
        }
        return ResultUtil.error("0001", "错误");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Message itemDelete(@RequestBody @Valid DeleteAllRequest deleteAllList){
        try{
            for(int i = 0;i < deleteAllList.getDeleteAllList().size();i++) {
                DeleteAll deleteAll = deleteAllList.getDeleteAllList().get(i);
                String userId = deleteAll.getUser_id();
                String createTime = deleteAll.getCreate_time();
                ItemResponse result = userService.itemDelete(userId, createTime);
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
