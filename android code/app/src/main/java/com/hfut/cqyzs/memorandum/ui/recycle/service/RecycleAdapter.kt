package com.hfut.cqyzs.memorandum.ui.recycle.service

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.utils.tool.OnItemClickListener
import kotlinx.android.synthetic.main.layout_recycle_item.view.*

class RecycleAdapter(val context: Context, private val itemView: Int, private val trashListItem: ArrayList<RecycleListItem>,
                     private val onItemClickListener: OnItemClickListener):
        RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewHolder {
        return RecycleViewHolder(LayoutInflater.from(context).inflate(itemView, parent, false))
    }

    override fun getItemCount(): Int {
        return trashListItem.size
    }

    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        holder.trashTitle.text = trashListItem[position].title
        when {
            trashListItem[position].type == 0 -> holder.trashType.setImageResource(R.drawable.task)
            trashListItem[position].type == 1 -> holder.trashType.setImageResource(R.drawable.record)
            else -> holder.trashType.setImageResource(R.drawable.list)
        }
        holder.trashRecover.setImageResource(R.drawable.recover)
        holder.trash.setImageResource(R.drawable.trash)
        holder.trash.setOnClickListener {
            onItemClickListener.onTrashClick(position)
        }
        holder.trashRecover.setOnClickListener {
            onItemClickListener.onRecover(position)
        }
    }

    inner class RecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val trashTitle: TextView =itemView.tv_title
        val trashType: ImageView = itemView.iv_trash_type
        val trashRecover: ImageView = itemView.iv_recover
        val trash: ImageView = itemView.iv_trash
    }

}