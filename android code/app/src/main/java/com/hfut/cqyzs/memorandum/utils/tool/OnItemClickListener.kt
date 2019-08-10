package com.hfut.cqyzs.memorandum.utils.tool

interface OnItemClickListener {
    fun onTrashClick(position: Int)
    fun onItemClick(position: Int)
    fun onRecover(position: Int)
}