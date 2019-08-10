package com.hfut.cqyzs.memorandum.bean;

import lombok.Data;

import java.util.List;

@Data
public class DeleteAllRequest {
    List<DeleteAll> deleteAllList;
}
