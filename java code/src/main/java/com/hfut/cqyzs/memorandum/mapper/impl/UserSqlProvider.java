package com.hfut.cqyzs.memorandum.mapper.impl;


import org.apache.ibatis.jdbc.SQL;

    public class UserSqlProvider {

        public String userTel(){
            return new SQL(){{
                UPDATE("user");
                SET("telephone = #{telephone}");
                WHERE("user_id = #{user_id}");
            }}.toString();
        }

        public String userEmail(){
            return new SQL(){{
                UPDATE("user");
                SET("email = #{email}");
                WHERE("user_id = #{user_id}");
            }}.toString();
        }

        public String userLogin(){
            return new SQL(){{
                SELECT("*");
                FROM("user");
                WHERE("user_id = #{user_id}");
                WHERE("user_psw = #{user_psw}");
            }}.toString();
        }

        public String userRegister(){
            return new SQL(){{
                INSERT_INTO("user");
                VALUES("user_id,user_psw,telephone,email,avatar",
                        "#{userID}, #{psw}, #{telephone}, #{email}, #{avatar}");
            }}.toString();
        }

        public String userResetPsw(){
            return new SQL(){{
                UPDATE("user");
                SET("user_psw = #{user_psw}");
                WHERE("user_id = #{user_id}");
            }}.toString();
        }

        /**
         * 插入记录
         * @return
         */
        public String recordInsert() {
            return new SQL() {
                {
                    INSERT_INTO("record");
                    VALUES("user_id, item_id, title, is_trash, is_item_trash, create_time, background, status",
                            "#{user_id}, #{item_id}, #{title}, #{is_trash}, #{is_item_trash}, #{create_time}, #{background}, #{status}");
                }
            }.toString();
        }

        /**
         * 插入清单
         * @return
         */
        public String itemInsert() {
            return new SQL() {
                {
                    INSERT_INTO("item");
                    VALUES("user_id, name, is_trash, status, create_time",
                            "#{user_id}, #{name}, #{is_trash}, #{status}, #{create_time}");
                }
            }.toString();
        }

        /**
         * 插入任务
         * @return
         */
        public String taskInsert() {
            return new SQL() {
                {
                    INSERT_INTO("task");
                    VALUES("user_id, item_id, title, priority, description, clock, deadline," +
                            "is_trash, is_item_trash, state, create_time, status",
                            "#{user_id},#{item_id},#{title},#{priority},#{description},#{clock}," +
                                    "#{deadline},#{is_trash},#{is_item_trash},#{state},#{create_time},#{status}");
                }
            }.toString();
        }

        /**
         * 插入记录的内容
         * @return
         */
        public String contentInsert() {
            return new SQL() {
                {
                    INSERT_INTO("content");
                    VALUES("record_id, record_content, content_type, content_order",
                            "#{record_id}, #{record_content}, #{content_type}, #{content_order}");
                }
            }.toString();
        }

        /**
         * 选择记录的内容
         * @return
         */
        public String contentSelect() {
            return new SQL() {
                {
                    SELECT("record_id, record_content, content_type, content_order");
                    FROM("content");
                    WHERE("record_id = #{record_id}");
                }
            }.toString();
        }


        public String recordIdSelect() {
            return new SQL() {
                {
                    SELECT("record_id");
                    FROM("record");
                    WHERE("user_id = #{user_id}");
                }
            }.toString();
        }

        public String timeSelect() {
            return new SQL() {
                {
                    SELECT("create_time");
                    FROM("record");
                    WHERE("record_id = #{record_id}");
                }
            }.toString();
        }

        /**
         * 选择任务
         * @return
         */
        public String taskSelect() {
            return new SQL() {
                {
                    SELECT("user_id, item_id, title, priority, description, clock, deadline," +
                            "is_trash, is_item_trash, state, create_time, status");
                    FROM("task" );
                    WHERE("user_id = #{userId}");
                }
            }.toString();
        }

        /**
         * 选择记录
         * @return
         */
        public String recordSelect() {
            return new SQL() {
                {
                    SELECT("user_id, item_id, title, is_trash, is_item_trash, create_time, background, status");
                    FROM("record");
                    WHERE("user_id = #{userId}");
                }
            }.toString();
        }

        /**
         * 选择清单
         * @return
         */
        public String itemSelect() {
            return new SQL() {
                {
                    SELECT("user_id, name, is_trash, status, create_time");
                    FROM("item");
                    WHERE("user_id = #{userId}");
                }
            }.toString();
        }

        /**
         * 更新记录
         * @return
         */
        public String recordUpdate() {
            return new SQL() {
                {
                    UPDATE("record");
                    SET("item_id = #{item_id}, title = #{title}, is_trash = #{is_trash}," +
                            " is_item_trash = #{is_item_trash}, background = #{background}, status = #{status}");
                    WHERE("user_id = #{user_id} and create_time = #{create_time}");
                }
            }.toString();
        }

        /**
         * 更新清单
         * @return
         */
        public String itemUpdate() {
            return new SQL() {
                {
                    UPDATE("item");
                    SET("name = #{name}, is_trash = #{is_trash}, status = #{status}");
                    WHERE("user_id = #{user_id} and create_time = #{create_time}");
                }
            }.toString();
        }

        /**
         * 更新任务
         * @return
         */
        public String taskUpdate() {
            return new SQL() {
                {
                    UPDATE("task");
                    SET("item_id = #{item_id}, title = #{title}, " +
                            "priority = #{priority}, description = #{description}, clock = #{clock} ," +
                            " deadline = #{deadline},is_trash = #{is_trash}, is_item_trash = #{is_item_trash}, " +
                            "state = #{state}, status = #{status}");
                    WHERE("user_id = #{user_id} and create_time = #{create_time}");
                }
            }.toString();
        }

        /**
         * 更新记录的内容
         * @return
         */
        public String contentUpdate() {
            return new SQL() {
                {
                    UPDATE("content");
                    SET("record_id = #{record_id}, record_content = #{record_content} , " +
                                    "content_type = #{content_type}, content_order = #{content_order}");
                }
            }.toString();
        }

        /**
         * 删除清单
         * @return
         */
        public String itemDelete() {
            return new SQL() {
                {
                    DELETE_FROM("item");
                    WHERE("user_id = #{user_id} and create_time = #{create_time}");
                }
            }.toString();
        }

        /**
         * 删除任务
         * @return
         */
        public String taskDelete() {
            return new SQL() {
                {
                    DELETE_FROM("task");
                    WHERE("user_id = #{user_id} and create_time = #{create_time}");
                }
            }.toString();
        }

        /**
         * 删除记录
         * @return
         */
        public String recordDelete() {
            return new SQL() {
                {
                    DELETE_FROM("record");
                    WHERE("user_id = #{user_id} and create_time = #{create_time}");
                }
            }.toString();
        }

        /**
         * 删除记录的内容
         * @return
         */

        public String contentDelete() {
            return new SQL() {
                {
                    DELETE_FROM("content");
                    WHERE("record_id = #{record_id}");
                }
            }.toString();
        }


        /**
         * 根据记录的id查找记录的创建时间
         * @return
         */
        public String recordSearchItemTime() {
            return new SQL() {
                {
                    SELECT("create_time");
                    FROM("item");
                    WHERE("item_id = #{item_id}");
                }
            }.toString();
        }

        /**
         * 根据任务的id查找记录的创建时间
         * @return
         */
        public String taskSearchItemTime() {
            return new SQL() {
                {
                    SELECT("create_time");
                    FROM("item");
                    WHERE("item_id = #{item_id}");
                }
            }.toString();
        }


        public String timeSearchItemId() {
            return new SQL() {
                {
                    SELECT("item_id");
                    FROM("item");
                    WHERE("create_time = #{create_time}");
                }
            }.toString();
        }

        public String timeSearchRecordId() {
            return new SQL() {
                {
                    SELECT("record_id");
                    FROM("record");
                    WHERE("create_time = #{create_time}");
                }
            }.toString();
        }

        public String recordIdDeleteSelect() {
            return new SQL() {
                {
                    SELECT("record_id");
                    FROM("record");
                    WHERE("user_id = #{user_id} and create_time = #{create_time}");
                }
            }.toString();
        }

        public String userIdIsHave() {
            return new SQL() {
                {
                    SELECT("count(*)");
                    FROM("user");
                    WHERE("user_id = #{user_id}");
                }
            }.toString();
        }

        public String telephoneIsRegister() {
            return new SQL() {
                {
                    SELECT("count(*)");
                    FROM("user");
                    WHERE("telephone = #{telephone}");
                }
            }.toString();
        }

        public String getTelephoneByUserId(){
            return new SQL() {
                {
                    SELECT("telephone");
                    FROM("user");
                    WHERE("user_id = #{user_id}");
                }
            }.toString();
        }

    }
