package com.hfut.cqyzs.memorandum.bean.content;

import lombok.Data;

import java.util.List;

@Data
public class ContentList {
    List<Content> contents;
    String recordCreateTime;
}
