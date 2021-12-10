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

class AccountAdapter(private val onAccountClick:(position:Int,account:Account?,isLastIndex:Boolean)->Unit):RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Account>() {
         override fun areContentsTheSame(oldItem: Account, newItem: Account) = oldItem.userId == newItem.userId

         override fun areItemsTheSame(oldItem: Account, newItem: Account) = oldItem.userId == newItem.userId

     }

    inner class AccountViewHolder(private val binding:RowAccountBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(account:Account?,isLastIndex:Boolean){
            binding.apply {
                if(!isLastIndex)
                    setAccount(account)
                else
                    ivProfile.setImageResource(R.drawable.ic_add)

                root.setOnClickListener {
                   onAccountClick(adapterPosition,account,isLastIndex)
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
        val isLastIndex = position == differ.currentList.size
        val account = if (!isLastIndex) differ.currentList[position] else null
        holder.bind(account,isLastIndex)
    }

    override fun getItemCount() = differ.currentList.size + 1


    private  val differ = AsyncListDiffer(this,differCallback)

    fun submitList(accounts:ArrayList<Account>){
        differ.submitList(accounts)
    }


}