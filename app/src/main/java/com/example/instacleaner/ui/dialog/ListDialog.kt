package com.example.instacleaner.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.instacleaner.R
import com.example.instacleaner.adapters.ListDialogAdapter
import com.example.instacleaner.data.local.ListDialogModel
import com.example.instacleaner.databinding.DialogListBinding
import com.example.instacleaner.utils.setDialogBackground


class ListDialog (private val pair: Pair<String,List<ListDialogModel>>, private val onOptionSelect:(option:ListDialogModel.Options)->Unit) : DialogFragment() {
    private var  _binding:DialogListBinding? = null
    private val binding:DialogListBinding
    get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_list, container,false)
        if (tag?.isEmpty() == true) binding.title.isVisible = false
        setUpRecyclerView()
        setDialogBackground()
        binding.title.text = pair.first
        return binding.root
    }



    private fun setUpRecyclerView(){
        binding.rvDialogList.adapter = ListDialogAdapter(pair.second){
            onOptionSelect(it.option)
            dismiss()
        }
        binding.rvDialogList.itemAnimator?.changeDuration = 0
    }


}