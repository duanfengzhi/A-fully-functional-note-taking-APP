package com.hfut.cqyzs.memorandum.ui.home.service

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfut.cqyzs.memorandum.local.entity.Item
import com.hfut.cqyzs.memorandum.utils.tool.OnItemClickListener
import kotlinx.android.synthetic.main.layout_item.view.*

class ItemAdapter(val context: Context, private val itemView: Int, private val listItem: List<Item>,
                  val onItemClickListener: OnItemClickListener):RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(itemView, parent,false))
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.tv_item_name.text = listItem[position].name
        if(listItem[position].status == 10){
            holder.iv_item_trash.visibility = View.GONE
        }
        holder.iv_item_trash.setOnClickListener{
            onItemClickListener.onTrashClick(position)

        }
        holder.itemView.setOnClickListener{
            onItemClickListener.onItemClick(position)
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tv_item_name:TextView = itemView.tv_item_name
        val iv_item_trash:ImageView = itemView.iv_item_trash
    }

}