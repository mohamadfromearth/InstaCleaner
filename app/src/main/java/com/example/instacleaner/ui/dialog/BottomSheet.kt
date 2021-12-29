package com.example.instacleaner.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.example.instacleaner.R
import com.example.instacleaner.adapters.ListDialogAdapter
import com.example.instacleaner.data.local.ListDialogModel
import com.example.instacleaner.databinding.DialogBottomSheetBinding
import com.example.instacleaner.databinding.DialogListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet(private val pair: Pair<String,List<ListDialogModel>>, private val onOptionSelect:(option: ListDialogModel.Options)->Unit):BottomSheetDialogFragment() {

    private var _binding:DialogBottomSheetBinding? = null
    private val binding:DialogBottomSheetBinding
    get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_bottom_sheet,container,false)
        setDialogBackground()
        setUpRecyclerView()

        return binding.root
    }


    private fun setDialogBackground(){
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        }
    }


    private fun setUpRecyclerView(){
        binding.rvDialogList.adapter = ListDialogAdapter(pair.second){
            onOptionSelect(it.option)
        }
        binding.rvDialogList.itemAnimator?.changeDuration = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}