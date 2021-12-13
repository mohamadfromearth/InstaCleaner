package com.example.instacleaner.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instacleaner.R
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.databinding.RowFollowBinding

class FollowAdapter(private val onUserClick:(user:User) ->Unit): ListAdapter<User,FollowAdapter.FollowViewHolder>(DiffCallback()) {

    inner class FollowViewHolder(private val binding:RowFollowBinding):RecyclerView.ViewHolder(binding.root){
          fun bind(user:User){
           binding.apply {
               setUser(user)
               root.setOnClickListener {
                   onUserClick(user)
               }
           }
          }
      }


    private class DiffCallback : DiffUtil.ItemCallback<User>() {

            override fun areItemsTheSame(oldItem: User, newItem: User) =
                oldItem.pk == newItem.pk


            override fun areContentsTheSame(oldItem: User, newItem: User) =
                oldItem == newItem
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
       return FollowViewHolder(
           DataBindingUtil.inflate(
               LayoutInflater.from(parent.context),
               R.layout.row_follow,
               parent,
               false
           )
       )
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}
