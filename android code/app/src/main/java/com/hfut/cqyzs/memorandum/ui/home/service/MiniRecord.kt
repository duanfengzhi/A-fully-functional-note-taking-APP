package com.hfut.cqyzs.memorandum.ui.home.service

import com.hfut.cqyzs.memorandum.local.entity.Record
import com.hfut.cqyzs.memorandum.local.entity.Task

class MiniRecord: ListItem {
    var record:Record? = null
    constructor(record:Record) : super() {
        this.record = record

        super.type = 1
        super.priority = null
        super.id = record.recordId
        super.title = record.title
        super.createTime = record.createTime
    }
}