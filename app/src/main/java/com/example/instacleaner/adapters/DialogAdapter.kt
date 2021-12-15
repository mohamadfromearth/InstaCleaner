package com.example.instacleaner.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instacleaner.R
import com.example.instacleaner.data.local.dialogModel.DialogModel
import com.example.instacleaner.databinding.RowDialogBinding

class DialogAdapter(private val onClick:(dialogModel:DialogModel)->Unit):ListAdapter<DialogModel,DialogAdapter.DialogViewHolder>(DiffCallback()){



    inner class DialogViewHolder(private val binding: RowDialogBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(dialogModel: DialogModel){
         binding.apply {
             setDialogModel(dialogModel)
             root.setOnClickListener {
                 onClick(currentList[bindingAdapterPosition])
             }
         }
        }
    }



    private class DiffCallback : DiffUtil.ItemCallback<DialogModel>() {


        override fun areItemsTheSame(oldItem: DialogModel, newItem: DialogModel) =
            oldItem == newItem


        override fun areContentsTheSame(oldItem: DialogModel, newItem: DialogModel) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        return DialogViewHolder(DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_dialog,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


}