package com.hfut.cqyzs.memorandum.controller;


import com.hfut.cqyzs.memorandum.utils.Message;
import com.hfut.cqyzs.memorandum.utils.ResultUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;

@RestController
public class UpLoad {

    // 上传多个文件
    @ResponseBody
    @RequestMapping(value="/icon",method= RequestMethod.POST)
    public Message upLoad(@RequestParam MultipartFile icon, HttpSession session) throws Exception {
            if (icon.getSize() > 0) {
                String path = session.getServletContext().getRealPath("/notepadres");
                String uploadDir = "C:\\File\\notepadres";
                String filename = icon.getOriginalFilename();
                File serverFile = new File(uploadDir +"/"+ filename);
                icon.transferTo(serverFile);
                System.out.println(uploadDir +"/"+ filename);

                return ResultUtil.success();
            }
        return ResultUtil.error("100","失败");
    }

}
