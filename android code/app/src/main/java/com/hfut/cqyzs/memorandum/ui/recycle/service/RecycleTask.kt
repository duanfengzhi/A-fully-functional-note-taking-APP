package com.hfut.cqyzs.memorandum.ui.recycle.service

import com.hfut.cqyzs.memorandum.local.entity.Task

class RecycleTask: RecycleListItem {
    var task: Task? = null
    constructor(task:Task) : super() {
        this.task = task

        super.type = 0
        super.id = task.task_id
        super.title = task.title
        super.createTime = task.createTime
    }
}