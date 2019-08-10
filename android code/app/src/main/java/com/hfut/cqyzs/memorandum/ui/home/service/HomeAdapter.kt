package com.hfut.cqyzs.memorandum.ui.home.service

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.utils.tool.OnItemClickListener
import kotlinx.android.synthetic.main.layout_home_item.view.*
import java.util.*
import kotlin.Comparator


class HomeAdapter(val context: Context, val itemView: Int, val listItem: List<ListItem>,
                  val onItemClickListener: OnItemClickListener) :
        RecyclerView.Adapter<HomeAdapter.HomeViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(context).inflate(itemView, parent,false))
    }

    override fun getItemCount(): Int {
        Collections.sort(listItem, object : Comparator<ListItem> {

            override fun compare(o1: ListItem, o2: ListItem): Int {
                return o1.createTime.toInt() - o2.createTime.toInt()
            }
        })
        return listItem.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        holder.title.text = listItem[position].title

        if (listItem[position].type == 0){
            holder.type.setImageResource(R.drawable.task)
        } else {
            holder.type.setImageResource(R.drawable.record)
        }
        if (listItem[position].priority != null && listItem[position].priority == 1){
            holder.importance.visibility = View.VISIBLE
        } else {
            holder.importance.visibility = View.GONE
        }
        holder.trash.setOnClickListener {
            onItemClickListener.onTrashClick(position)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }

    }


    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.tv_title
        val importance: ImageView = itemView.iv_important
        val type: ImageView = itemView.iv_trash_type
        val trash: ImageView = itemView.iv_trash
    }
}