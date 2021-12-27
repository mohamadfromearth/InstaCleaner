package com.example.instacleaner.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.instacleaner.R
import com.example.instacleaner.data.local.ListDialogModel
import com.example.instacleaner.databinding.RowListDialogBinding

class ListDialogAdapter(private val options:List<ListDialogModel>,private val itemClick:(listDialogModel:ListDialogModel)->Unit):RecyclerView.Adapter<ListDialogAdapter.DialogListViewHolder>() {



    inner class DialogListViewHolder(val binding:RowListDialogBinding):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(option:ListDialogModel){
            binding.apply {
                rowListBtn.icon = binding.root.context.getDrawable(option.icon)
                rowListBtn.text = option.title
                rowListBtn.setOnClickListener {
                    itemClick(option)
                }
            }


        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DialogListViewHolder {
        return DialogListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.context),
                R.layout.row_list_dialog,
                viewGroup,false
            )
        )
    }

    override fun onBindViewHolder(holder: DialogListViewHolder, position: Int) {
        holder.bind(options[position])

    }

    override fun getItemCount(): Int {
       return options.size
    }


}