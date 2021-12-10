package com.example.instacleaner.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.instacleaner.R
import com.example.instacleaner.data.local.Account
import com.example.instacleaner.databinding.RowAccountBinding

class AccountAdapter(private val onAccountClick:(position:Int,account:Account)->Unit):RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Account>() {
         override fun areContentsTheSame(oldItem: Account, newItem: Account) = oldItem.userId == newItem.userId

         override fun areItemsTheSame(oldItem: Account, newItem: Account) = oldItem.userId == newItem.userId

     }

    inner class AccountViewHolder(private val binding:RowAccountBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(account:Account){
            binding.apply {
               setAccount(account)
               root.setOnClickListener {
                   onAccountClick(adapterPosition,account)
               }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AccountViewHolder(DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.row_account,
        parent,
        false
    ))

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size


    private  val differ = AsyncListDiffer(this,differCallback)

    fun submitList(account:List<Account>){
        differ.submitList(account)
    }


}