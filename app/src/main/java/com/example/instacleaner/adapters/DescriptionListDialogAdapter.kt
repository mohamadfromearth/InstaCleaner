package com.example.instacleaner.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.instacleaner.R
import com.example.instacleaner.data.local.DescriptionDialogModel
import com.example.instacleaner.databinding.RowDialogDescriptionListBinding

class DescriptionListDialogAdapter(private val pagingStateList:List<DescriptionDialogModel>,private val onClick:(option:DescriptionDialogModel.Options)->Unit):RecyclerView.Adapter<DescriptionListDialogAdapter.DescriptionViewHolder>() {




    inner class DescriptionViewHolder(private val binding:RowDialogDescriptionListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(descriptionModel:DescriptionDialogModel){
            binding.apply {
             description = descriptionModel
             rowDescriptionBtn.setIconResource(descriptionModel.icon)
             rowDescriptionBtn.setOnClickListener {
                 onClick(descriptionModel.options)
             }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        return DescriptionViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_dialog_description_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
      holder.bind(pagingStateList[position])
    }

    override fun getItemCount(): Int {
        return pagingStateList.size
    }

}