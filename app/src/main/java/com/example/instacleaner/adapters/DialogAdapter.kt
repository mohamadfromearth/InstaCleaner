package com.example.instacleaner.adapters


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instacleaner.R
import com.example.instacleaner.data.local.DialogModel

import com.example.instacleaner.databinding.RowDialogBinding

class DialogAdapter(private val onClick : (dialogmodel:DialogModel,pos:Int)-> Unit):ListAdapter<DialogModel,DialogAdapter.DialogViewHolder>(DiffCallback()){



    inner class DialogViewHolder(val binding: RowDialogBinding):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(dialogModel: DialogModel){
         binding.apply {
             setDialogModel(dialogModel)
             root.setOnClickListener {
                 onClick(dialogModel,bindingAdapterPosition)
             }
             if (dialogModel.isSelected) {
                 dialogCard.setCardBackgroundColor(binding.root.context.getColor(R.color.md_green_500))
                 tvRowDialogText.setTextColor(Color.parseColor("#FFFFFF"))

             } else{
                 dialogCard.setCardBackgroundColor(binding.root.context.getColor(R.color.design_default_color_on_primary))
                 tvRowDialogText.setTextColor(Color.parseColor("#000000"))
                 //ivCheck.setBackgroundResource(R.drawable.ic_check_circle)
             }

         }
        }
    }




//     var onItemClick:((dialogmodel:DialogModel,pos:Int)-> Unit) ={}



    companion object{
        class DiffCallback : DiffUtil.ItemCallback<DialogModel>() {


            override fun areItemsTheSame(oldItem: DialogModel, newItem: DialogModel) =
                oldItem == newItem


            override fun areContentsTheSame(oldItem: DialogModel, newItem: DialogModel) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        return DialogViewHolder(DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_dialog,
            parent,
            false
        ))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
       holder.bind(currentList[position])
    }


}