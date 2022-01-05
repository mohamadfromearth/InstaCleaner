package com.example.instacleaner.adapters

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
               if (user.isSelected){
                   ivCheck.visibility = View.VISIBLE
                   binding.cvFollow.setCardBackgroundColor(binding.root.context.getColor(R.color.design_default_color_secondary_variant))
                   binding.cvFollow.startAnimation(AnimationUtils.loadAnimation(binding.root.context,R.anim.anim_scale_up))

               } else {
                   ivCheck.visibility = View.GONE
                   binding.cvFollow.setCardBackgroundColor(binding.root.context.getColor(R.color.colorPrimary))
               }
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








    inner class LoadingViewHolder(binding:RowLoadingBinding):RecyclerView.ViewHolder(binding.root){


    }

    override fun getItemViewType(position: Int): Int {
        if (currentList[position].pk == -1L){
            return TYPE_LOADING
        }else{
            return super.getItemViewType(position)
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User) =
            oldItem.pk == newItem.pk


        override fun areContentsTheSame(oldItem: User, newItem: User) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == TYPE_LOADING){
          LoadingViewHolder(
              DataBindingUtil.inflate(
                  LayoutInflater.from(parent.context),
                  R.layout.row_loading,
                  parent,
                  false
              )
          )
        }else{
            FollowViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_follow,
                    parent,
                    false
                )
            )
        }



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
