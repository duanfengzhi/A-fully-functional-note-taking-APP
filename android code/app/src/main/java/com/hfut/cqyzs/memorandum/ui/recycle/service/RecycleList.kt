package com.hfut.cqyzs.memorandum.ui.recycle.service

import com.hfut.cqyzs.memorandum.local.entity.Item

class RecycleList: RecycleListItem {
    var item: Item? = null
    constructor(item: Item): super() {
        this.item = item

        super.id = item.itemId
        super.title = item.name
        super.type = 2
        super.createTime = null
    }

}