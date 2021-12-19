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
import com.example.instacleaner.data.local.dialogModel.Tab
import com.example.instacleaner.databinding.DialogMainBinding
import com.example.instacleaner.utils.setChildTypeface
import com.google.android.material.tabs.TabLayout

//val scale: Float = resources.displayMetrics.density
//// Convert the dps to pixels, based on density scale
//mGestureThreshold = (GESTURE_THRESHOLD_DP * scale + 0.5f).toInt()


class MainDialog(
    private val dialogModels:Pair<String,ArrayList<DialogModel>>,
    private val callBack:(dialogModel:DialogModel)->Unit
                 ):DialogFragment() {

    private lateinit var dialogAdapter:DialogAdapter

    private var tabIndex  = 0

//   private var isSingleTab = false

    private var _binding:DialogMainBinding? = null
    private val binding:DialogMainBinding
    get() = _binding!!

    var filter : DialogModel.FilterType  = dialogModels.second.first { it.isSelected }.filter


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

        binding.dialogTab.getTabAt(tabIndex)?.select()


        binding.dialogTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
              tab?.let {
                  tabIndex = it.position
                  filter = when(filter){
                      is DialogModel.FilterType.Status -> DialogModel.FilterType.Status(tabIndex == 0)
                      is DialogModel.FilterType.Avatar -> DialogModel.FilterType.Avatar(tabIndex == 0)
                      is DialogModel.FilterType.Verify -> DialogModel.FilterType.Verify(tabIndex == 0)
                      is DialogModel.FilterType.Select -> DialogModel.FilterType.Select(tabIndex == 0)
                      is DialogModel.FilterType.FollowBack -> DialogModel.FilterType.FollowBack(tabIndex == 0)
                      is DialogModel.FilterType.NoFilter -> DialogModel.FilterType.NoFilter
                  }

              }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.btnOk.setOnClickListener {
            val dialogModel = dialogModels.second.first { it.isSelected }
            callBack(dialogModel.copy(filter = filter))
            dismiss()
        }

        return binding.root
    }



    private  fun setDataToViews(){
     binding.dialogTab.removeAllTabs()
    val dialogModel =dialogModels.second.first { it.isSelected }
    dialogModel.tabs.forEach {
        binding.dialogTab.addTab(createTab(it.title))
    }


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




    private fun setUpRecyclerView() {
        dialogAdapter = DialogAdapter { dialogModel, pos ->
            //remove all tabs
            //for each tab, create a tab
            when (dialogModel.filter) {
                is DialogModel.FilterType.Status -> filter = DialogModel.FilterType.Status(tabIndex == 0)
                is DialogModel.FilterType.Avatar -> filter = DialogModel.FilterType.Avatar(tabIndex == 0)
                is DialogModel.FilterType.Verify -> filter = DialogModel.FilterType.Verify(tabIndex == 0)
                is DialogModel.FilterType.Select -> filter = DialogModel.FilterType.Select(tabIndex == 0)
                is DialogModel.FilterType.FollowBack -> filter = DialogModel.FilterType.FollowBack(tabIndex == 0)
                is DialogModel.FilterType.NoFilter -> filter = DialogModel.FilterType.NoFilter
            }

            binding.dialogTab.removeAllTabs()
            dialogModel.tabs.forEach {
                      binding.dialogTab.addTab(createTab(it.title))

                }

            val dialogList =  dialogModels.second.cloned()
            dialogList.forEach {
                it.isSelected = false
            }
            dialogList[pos].isSelected = true

            dialogAdapter.submitList(dialogList)
        }
        binding.dialogRv.adapter = dialogAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}