package com.hfut.cqyzs.memorandum.mapper;

import com.hfut.cqyzs.memorandum.bean.content.Content;
import com.hfut.cqyzs.memorandum.bean.content.ContentAddTime;
import com.hfut.cqyzs.memorandum.bean.item.Item;
import com.hfut.cqyzs.memorandum.bean.record.Record;
import com.hfut.cqyzs.memorandum.bean.task.Task;
import com.hfut.cqyzs.memorandum.entity.User;
import com.hfut.cqyzs.memorandum.mapper.impl.UserSqlProvider;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserMapper {

    @UpdateProvider(type = UserSqlProvider.class, method = "userTel")
    int userTel(@Param("user_id") String user_id, @Param("telephone") String telephone);

    @UpdateProvider(type = UserSqlProvider.class, method = "userEmail")
    int userEmail(@Param("user_id") String user_id, @Param("email") String email);

    @SelectProvider(type = UserSqlProvider.class, method = "userLogin")
    User userLogin(@Param("user_id") String user_id, @Param("user_psw") String user_psw);

    @InsertProvider(type = UserSqlProvider.class, method = "userRegister")
    int userRegister(@Param("userID") String userID, @Param("psw") String psw,
                     @Param("telephone") String telephone, @Param("email") String email,
                     @Param("avatar") String avatar);

    @UpdateProvider(type = UserSqlProvider.class, method = "userResetPsw")
    int userResetPsw(@Param("user_id") String user_id, @Param("user_psw") String user_psw);

    @InsertProvider(type = UserSqlProvider.class, method = "recordInsert")
    int recordInsert(@Param("user_id")String user_id, @Param("item_id")int item_id,
                     @Param("title")String title, @Param("is_trash")int is_trash, @Param("is_item_trash")int is_item_trash,
                     @Param("create_time")String create_time, @Param("background")int background, @Param("status")int status);

    @InsertProvider(type = UserSqlProvider.class, method = "itemInsert")
    int itemInsert(@Param("user_id")String user_id, @Param("name")String name,
                   @Param("is_trash")int is_trash, @Param("status")int status, @Param("create_time")String create_time);

    @InsertProvider(type = UserSqlProvider.class, method = "taskInsert")
    int taskInsert(@Param("user_id")String user_id, @Param("item_id")int item_id, @Param("title")String title,
                   @Param("priority")int priority, @Param("description")String description, @Param("clock")String clock,
                   @Param("deadline")String deadline, @Param("is_trash")int is_trash, @Param("is_item_trash")int is_item_trash,
                   @Param("state")int state, @Param("create_time")String create_time, @Param("status")int status);
    @InsertProvider(type = UserSqlProvider.class, method = "contentInsert")
    int contentInsert(@Param("record_id")int record_id, @Param("record_content")String record_content,
                      @Param("content_type")int content_type, @Param("content_order")int content_order);

    @SelectProvider(type = UserSqlProvider.class, method = "recordSelect")
    List<Record> recordSelect(@Param("userId")String userId);
    @SelectProvider(type = UserSqlProvider.class, method = "itemSelect")
    List<Item> itemSelect(@Param("userId")String userId);
    @SelectProvider(type = UserSqlProvider.class, method = "taskSelect")
    List<Task> taskSelect(@Param("userId")String userId);
    @SelectProvider(type = UserSqlProvider.class, method = "contentSelect")
    List<Content> contentSelect(@Param("record_id")int record_id);

    @UpdateProvider(type = UserSqlProvider.class, method = "recordUpdate")
    int recordUpdate(@Param("user_id")String user_id, @Param("item_id")int item_id,
                     @Param("title")String title, @Param("is_trash")int is_trash, @Param("is_item_trash")int is_item_trash,
                     @Param("create_time")String create_time, @Param("background")int background, @Param("status")int status);

    @UpdateProvider(type = UserSqlProvider.class, method = "itemUpdate")
    int itemUpdate(@Param("user_id")String user_id, @Param("name")String name, @Param("is_trash")int is_trash,
                   @Param("status")int status,@Param("create_time")String create_time);

    @UpdateProvider(type = UserSqlProvider.class, method = "taskUpdate")
    int taskUpdate(@Param("user_id")String user_id, @Param("item_id")int item_id, @Param("title")String title,
                   @Param("priority")int priority, @Param("description")String description, @Param("clock")String clock,
                   @Param("deadline")String deadline, @Param("is_trash")int is_trash, @Param("is_item_trash")int is_item_trash,
                   @Param("state")int state, @Param("create_time")String create_time, @Param("status")int status);
    @UpdateProvider(type = UserSqlProvider.class, method = "contentUpdate")
    int contentUpdate(@Param("record_id")int record_id, @Param("record_content")String record_content,
                      @Param("content_type")int content_type, @Param("content_order")int content_order);

    @SelectProvider(type = UserSqlProvider.class, method = "recordSearchItemTime")
    String recordSearchItemTime(@Param("item_id")int item_id);
    @SelectProvider(type = UserSqlProvider.class, method = "taskSearchItemTime")
    String taskSearchItemTime(@Param("item_id")int item_id);

    @SelectProvider(type = UserSqlProvider.class, method = "recordIdSelect")
    List<Integer> recordIdSelect(@Param("user_id")String user_id);
    @SelectProvider(type = UserSqlProvider.class, method = "timeSelect")
    String timeSelect(@Param("record_id")int record_id);

    @DeleteProvider(type = UserSqlProvider.class, method = "itemDelete")
    int itemDelete(@Param("user_id")String user_id,@Param("create_time")String create_time);
    @DeleteProvider(type = UserSqlProvider.class, method = "taskDelete")
    int taskDelete(@Param("user_id")String user_id,@Param("create_time")String create_time);
    @DeleteProvider(type = UserSqlProvider.class, method = "recordDelete")
    int recordDelete(@Param("user_id")String user_id,@Param("create_time")String create_time);
    @DeleteProvider(type = UserSqlProvider.class, method = "contentDelete")
    int contentDelete(@Param("record_id")int record_id);

    @SelectProvider(type = UserSqlProvider.class, method = "timeSearchItemId")
    int timeSearchItemId(@Param("create_time")String create_time);
    @SelectProvider(type = UserSqlProvider.class, method = "timeSearchRecordId")
    int timeSearchRecordId(@Param("create_time")String create_time);

    @SelectProvider(type = UserSqlProvider.class, method = "recordIdDeleteSelect")
    int recordIdDeleteSelect(@Param("user_id")String user_id,@Param("create_time")String create_time);

    @SelectProvider(type = UserSqlProvider.class, method = "userIdIsHave")
    int userIdIsHave(@Param("user_id")String user_id);
    @SelectProvider(type = UserSqlProvider.class, method = "telephoneIsRegister")
    int telephoneIsRegister(@Param("telephone")String telephone);
    @SelectProvider(type = UserSqlProvider.class, method = "getTelephoneByUserId")
    String getTelephoneByUserId(@Param("user_id")String user_id);
}
