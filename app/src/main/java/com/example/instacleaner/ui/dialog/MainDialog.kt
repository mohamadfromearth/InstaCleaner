package com.example.instacleaner.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TableLayout
import androidx.compose.ui.input.key.Key.Companion.Tab
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.instacleaner.R
import com.example.instacleaner.adapters.DialogAdapter
import com.example.instacleaner.data.local.DialogModel
import com.example.instacleaner.data.local.DialogModel.Companion.cloned
import com.example.instacleaner.databinding.DialogMainBinding
import com.google.android.material.tabs.TabLayout


class MainDialog(
    private val dialogModels:Pair<String,ArrayList<DialogModel>>,
    private val callBack:(dialogModel:DialogModel)->Unit
                 ):DialogFragment() {

    private lateinit var dialogAdapter:DialogAdapter

    private var tabIndex  = 0

   private var isSingleTab = false

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
        setDialogBackground()
        setUpRecyclerView()
        setDataToViews()

        binding.dialogTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
              tab?.let {
                  tabIndex = it.position
              }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })


        dialogAdapter.onItemClick  = { dialogModel,pos  ->
            if (dialogModels.second.size -1 == pos){
                binding.dialogTab.getTabAt(1)?.let {
                    binding.dialogTab.removeTab(it)
                    binding.dialogTab.setBackgroundResource(R.drawable.bg_single_tab)
                    isSingleTab = true
                }
                binding.dialogTab.getTabAt(0)?.text = dialogModel.tabs[0].title
            }else{
                if (isSingleTab){
                  binding.dialogTab.setBackgroundResource(R.drawable.tab_bg)
                  binding.dialogTab.getTabAt(0)?.text = dialogModel.tabs[0].title
                  val tab2 = binding.dialogTab.newTab().setText(dialogModel.tabs[1].title)
                  binding.dialogTab.addTab(tab2)
                  isSingleTab = false
                }else{
                    binding.dialogTab.getTabAt(0)?.text = dialogModel.tabs[0].title
                    binding.dialogTab.getTabAt(1)?.text = dialogModel.tabs[1].title
                }
            }

            binding.dialogTab.getTabAt(0)?.text = dialogModel.tabs[0].title

            dialogModels.second.forEach {
                it.isSelected = false
            }
            dialogModels.second[pos].isSelected = true
            dialogAdapter.submitList(dialogModels.second.cloned())
        }

        binding.btnOk.setOnClickListener {
         val dialogModel = dialogModels.second.first { it.isSelected }
            dialogModel.tabs[tabIndex].isSelected = true
            callBack(dialogModel)
            dismiss()
        }




        return binding.root
    }

    private  fun setDataToViews(){
       val dialogModelList =dialogModels.second
        dialogModelList[0].isSelected = true
        val tab1 = binding.dialogTab.newTab().setText(dialogModelList[0].tabs[0].title)
        val tab2 = binding.dialogTab.newTab().setText(dialogModelList[0].tabs[1].title)
        binding.dialogTab.addTab(tab1)
        binding.dialogTab.addTab(tab2)
        dialogAdapter.submitList(dialogModels.second.cloned())
        binding.dialogTitle.text = dialogModels.first
    }

    private fun setDialogBackground(){
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }



    private fun setUpRecyclerView(){
        dialogAdapter = DialogAdapter()
        binding.dialogRv.adapter = dialogAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}