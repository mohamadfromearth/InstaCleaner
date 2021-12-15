package com.example.instacleaner.adapters


import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.instacleaner.R
import com.example.instacleaner.data.local.Account
import com.example.instacleaner.databinding.RowAccountBinding
import com.example.instacleaner.databinding.RowAddAccountBinding
import com.example.instacleaner.utils.log

class AccountAdapter(
    private val onAccountClick: (position: Int, account: Account) -> Unit,
    private val onAddAccountClick: () -> Unit,
) : ListAdapter<Account,ViewHolder>(DiffCallback()) {


    inner class AccountViewHolder(private val binding: RowAccountBinding) :
        ViewHolder(binding.root) {
        fun bind(account: Account) {
            log("HOLDER:NORMAL${account.isSelected}")
            binding.apply {
                setAccount(account)
                ivProfile.strokeWidth = if(account.isSelected) 8F else 0F
                root.setOnClickListener {
                    onAccountClick(bindingAdapterPosition, account)
                }
            }
        }
    }

    inner class AddAccountViewHolder(private val binding: RowAddAccountBinding) :
        ViewHolder(binding.root) {
        fun bind() {
            log("HOLDER:ADD")
            binding.apply {
                root.setOnClickListener {
                    onAddAccountClick()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       log("viewType$viewType")
       log("size${currentList.size}")
        return if (viewType == currentList.size && currentList.isNotEmpty()) {
            AddAccountViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_add_account,
                    parent,
                    false
                )
            )
        } else {
            AccountViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_account,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is AccountViewHolder -> holder.bind(currentList[position])
            is AddAccountViewHolder -> holder.bind()
        }
    }

    override fun getItemCount() = if(currentList.isEmpty()) 0 else currentList.size + 1


    companion object{

        class DiffCallback : DiffUtil.ItemCallback<Account>() {


            override fun areItemsTheSame(oldItem: Account, newItem: Account) =
                oldItem.user.pk == newItem.user.pk


            override fun areContentsTheSame(oldItem: Account, newItem: Account) =
                oldItem == newItem
        }

    }




}


