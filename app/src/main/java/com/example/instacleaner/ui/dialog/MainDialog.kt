package com.example.instacleaner.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.instacleaner.R
import com.example.instacleaner.adapters.DialogAdapter
import com.example.instacleaner.data.local.dialogModel.DialogModel
import com.example.instacleaner.databinding.DialogMainBinding
import com.google.android.material.tabs.TabLayout

class MainDialog(
    private val dialogModels:ArrayList<DialogModel>,
    private val callBack:()->Unit
                 ):DialogFragment() {

    private lateinit var dialogAdapter:DialogAdapter

    private var _binding:DialogMainBinding? = null
    private val binding:DialogMainBinding
    get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater,
        R.layout.dialog_main,
        container,
        false
            )
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        setUpRecyclerView()
        dialogAdapter.submitList(dialogModels)
        return binding.root
    }



//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//
//        super.onViewCreated(view, savedInstanceState)
////        _binding = DataBindingUtil.bind(view)
//
//        setUpRecyclerView()
//        dialogAdapter.submitList(dialogModels)
//    }



    private fun setUpRecyclerView(){
        dialogAdapter = DialogAdapter {
           binding.dialogTab.getTabAt(0)?.text = it.tabs[0].title
            if (it.tabs.size>1)
            binding.dialogTab.getTabAt(1)?.text = it.tabs[1].title
        }
        binding.dialogRv.adapter = dialogAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}