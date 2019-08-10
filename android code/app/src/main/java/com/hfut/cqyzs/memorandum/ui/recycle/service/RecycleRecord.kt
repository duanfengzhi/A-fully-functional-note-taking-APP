package com.hfut.cqyzs.memorandum.ui.recycle.service

import com.hfut.cqyzs.memorandum.local.entity.Record
import com.hfut.cqyzs.memorandum.local.entity.Task

class RecycleRecord: RecycleListItem {
    var record:Record? = null
    constructor(record:Record) : super() {
        this.record = record

        super.type = 1
        super.id = record.recordId
        super.title = record.title
        super.createTime = record.createTime
    }
}