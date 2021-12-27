package com.example.instacleaner.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.instacleaner.R
import com.example.instacleaner.adapters.DialogAdapter
import com.example.instacleaner.data.local.DialogModel
import com.example.instacleaner.data.local.DialogModel.Companion.cloned
import com.example.instacleaner.databinding.DialogMainBinding
import com.example.instacleaner.utils.setChildTypeface
import com.google.android.material.tabs.TabLayout


class TabDialog(
    private val dialogModels:Pair<String,ArrayList<DialogModel>>,
    private val callBack:(dialogModel:DialogModel)->Unit
                 ):DialogFragment() {

    private lateinit var dialogAdapter:DialogAdapter

    private var tabIndex  = 0

    private var _binding:DialogMainBinding? = null
    private val binding:DialogMainBinding
    get() = _binding!!

    var options : DialogModel.Options  = dialogModels.second.first { it.isSelected }.option


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
        setOptionState(options)

        binding.dialogTab.getTabAt(tabIndex)?.select()


        binding.dialogTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
              tab?.let {
                  tabIndex = it.position
               setOptionState(options)

              }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.btnOk.setOnClickListener {
            val dialogModel = dialogModels.second.first { it.isSelected }
            callBack(dialogModel.copy(option = options))
            dismiss()
        }

        return binding.root
    }


    private fun initTabState(){

    }


    private  fun setDataToViews(){
     binding.dialogTab.removeAllTabs()
    val dialogModel =dialogModels.second.first { it.isSelected }
    dialogModel.tabs.forEach {
        binding.dialogTab.addTab(createTab(it.title))
    }
        setTabViewBackground(dialogModel)


        dialogAdapter.submitList(dialogModels.second)
        binding.dialogTitle.text = dialogModels.first
    }

    private fun createTab(title:String) =
        binding.dialogTab.newTab().apply {
            val tp = ResourcesCompat.getFont(requireContext(), R.font.iran_sans_web_medium)
            text = title
            view?.setChildTypeface(tp)
        }

    private fun setDialogBackground(){
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            val width = (resources.displayMetrics.widthPixels * 0.20).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
            dialog?.window?.setLayout(width, height)
        }
    }

private fun setOptionState(option:DialogModel.Options){
    options = when (option) {
        is DialogModel.Options.Status -> DialogModel.Options.Status(tabIndex != 0)
        is DialogModel.Options.Avatar -> DialogModel.Options.Avatar(tabIndex == 0)
        is DialogModel.Options.Verify -> DialogModel.Options.Verify(tabIndex == 0)
        is DialogModel.Options.Select -> DialogModel.Options.Select(tabIndex == 0)
        is DialogModel.Options.FollowBack -> DialogModel.Options.FollowBack(tabIndex == 0)
        is DialogModel.Options.NoFilter -> DialogModel.Options.NoFilter
        is DialogModel.Options.ByUsername -> DialogModel.Options.ByUsername(tabIndex==0)
        is DialogModel.Options.ByCondition -> DialogModel.Options.ByCondition(tabIndex==0)
        is DialogModel.Options.ByAvatar -> DialogModel.Options.ByAvatar(tabIndex==0)
        is DialogModel.Options.BySelection -> DialogModel.Options.BySelection(tabIndex == 0)
        else-> DialogModel.Options.NoSort

    }
}


    private fun setUpRecyclerView() {
        dialogAdapter = DialogAdapter { dialogModel, pos ->
            //remove all tabs
            //for each tab, create a tab
         setOptionState(dialogModel.option)

            binding.dialogTab.removeAllTabs()
            dialogModel.tabs.forEach {
                      binding.dialogTab.addTab(createTab(it.title))
            }
            setTabViewBackground(dialogModel)

            val dialogList =  dialogModels.second.cloned()
            dialogList.forEach {
                it.isSelected = false
            }
            dialogList[pos].isSelected = true

            dialogAdapter.submitList(dialogList)
        }
        binding.dialogRv.adapter = dialogAdapter
    }


    private fun setTabViewBackground(dialogModel: DialogModel){
        if (dialogModel.tabs.size == 1){
            binding.dialogTab.setBackgroundResource(R.drawable.bg_single_tab)
        }else{
            binding.dialogTab.setBackgroundResource(R.drawable.tab_bg)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}