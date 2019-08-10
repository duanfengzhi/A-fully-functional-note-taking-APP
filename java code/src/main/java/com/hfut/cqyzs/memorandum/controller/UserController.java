package com.hfut.cqyzs.memorandum.controller;

import com.hfut.cqyzs.memorandum.bean.user.*;
import com.hfut.cqyzs.memorandum.service.*;
import com.hfut.cqyzs.memorandum.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping(value = "/account",method = RequestMethod.POST)
    public Message userAccount(@RequestBody @Valid UserAccountRequestBean requestBean, BindingResult bindingResult) {
        try{
            UserAccountResponseBean result = this.userService.userAccount(requestBean);
            return ResultUtil.success(result);
        }
        catch (Exception e){
            return ResultUtil.error("0001", e.getMessage());
        }
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Message userLogin(@RequestBody @Valid UserLoginRequestBean requestBean, BindingResult bindingResult) {
        try{
            UserLoginResponseBean result = this.userService.userLogin(requestBean);
            return ResultUtil.success(result);
        }
        catch (Exception e){
            return ResultUtil.error("0001", e.getMessage());
        }
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public Message userRegister(@RequestBody @Valid UserRegisterRequestBean requestBean) {
        try {
            UserRegisterResponseBean user_id_status = this.userService.userIdIsHave(requestBean.getUserID());
            if(user_id_status.getStatus() != 1){
                return ResultUtil.success(user_id_status);
            }
            UserRegisterResponseBean telephone_status = this.userService.telephoneIsRegister(requestBean.getTelephone());
            if(telephone_status.getStatus() != 1){
                return ResultUtil.success(telephone_status);
            }
            UserRegisterResponseBean result = this.userService.userRegister(requestBean);
            return ResultUtil.success(result);
        } catch (Exception e) {
            return ResultUtil.error("0001", e.getMessage());
        }
    }

    @RequestMapping(value = "/resetpsw",method = RequestMethod.POST)
    public Message userResetPsw(@RequestBody @Valid UserResetPswRequestBean requestBean, BindingResult bindingResult) {
        try{
            UserResetPswResponseBean result = this.userService.userResetPsw(requestBean);
            return ResultUtil.success(result);
        }
        catch (Exception e){
            return ResultUtil.error("0001", e.getMessage());
        }
    }

    @RequestMapping(value = "/getTelephone",method = RequestMethod.GET)
    public Message getTelephone(@RequestParam("user_id")String user_id) {
        UserPasswordResponse userPasswordResponse = new UserPasswordResponse();
        try{
            String telephone = this.userService.getTelephoneByUserId(user_id);
            userPasswordResponse.setTel(telephone);
            return ResultUtil.success(userPasswordResponse);
        }
        catch (Exception e){
            return ResultUtil.error("0001", e.getMessage());
        }
    }



}
