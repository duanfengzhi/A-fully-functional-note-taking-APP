package com.hfut.cqyzs.memorandum.bean.user;

import com.hfut.cqyzs.memorandum.entity.User;
import lombok.Data;

@Data
public class UserLoginResponseBean {
    int status;
    User user;
}
