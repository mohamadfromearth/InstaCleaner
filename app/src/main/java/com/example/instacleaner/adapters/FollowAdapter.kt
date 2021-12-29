package com.example.instacleaner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instacleaner.R
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.databinding.RowFollowBinding
import com.example.instacleaner.databinding.RowLoadingBinding

class FollowAdapter(private val onUserClick:(pos:Int,user:User) ->Unit,private val onOptionClick:(pos:Int,user:User) -> Unit,private val onLongClick:(pos:Int,user:User) -> Unit): ListAdapter<User,RecyclerView.ViewHolder>(DiffCallback()) {

    inner class FollowViewHolder(private val binding:RowFollowBinding):RecyclerView.ViewHolder(binding.root){
          fun bind(user:User){
           binding.apply {
               setUser(user)
               if (user.isSelected) ivCheck.visibility = View.VISIBLE else ivCheck.visibility = View.GONE
               if (user.is_verified) ivVerified.visibility = View.VISIBLE else ivVerified.visibility = View.GONE
               root.setOnClickListener {
                   onUserClick(bindingAdapterPosition,user)
               }
              root.setOnLongClickListener {
                  onLongClick(bindingAdapterPosition,user)
                  true
              }
               ivOption.setOnClickListener {
                   onOptionClick(bindingAdapterPosition,user)
               }

            }
        }
    }

    inner class LoadingViewHolder(private val binding:RowLoadingBinding):RecyclerView.ViewHolder(binding.root){

    }


    private class DiffCallback : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User) =
            oldItem.pk == newItem.pk


        override fun areContentsTheSame(oldItem: User, newItem: User) =
            oldItem.isSelected == newItem.isSelected
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return FollowViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_follow,
                    parent,
                    false
                )
            )



        }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is FollowViewHolder -> {
                holder.bind(currentList[position])
            }
        }

    }

    companion object{
        const val TYPE_LOADING = 3
    }

}
