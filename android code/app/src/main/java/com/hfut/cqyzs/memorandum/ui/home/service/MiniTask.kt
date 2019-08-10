package com.hfut.cqyzs.memorandum.ui.home.service

import com.hfut.cqyzs.memorandum.local.entity.Task

class MiniTask: ListItem {
    var task: Task? = null
    constructor(task:Task) : super() {
        this.task = task

        super.type = 0
        super.priority = task.priority
        super.id = task.task_id
        super.title = task.title
        super.createTime = task.createTime
    }
}