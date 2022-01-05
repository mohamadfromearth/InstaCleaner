package com.example.instacleaner.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.instacleaner.R
import com.example.instacleaner.adapters.DescriptionListDialogAdapter
import com.example.instacleaner.data.local.DescriptionDialogModel
import com.example.instacleaner.databinding.DialogDescriptionListBinding
import com.example.instacleaner.utils.setDialogBackground

class DescriptionDialog(private val descriptionList:List<DescriptionDialogModel>,private val callback:(option:DescriptionDialogModel.Options)->Unit):DialogFragment() {

    private var _binding:DialogDescriptionListBinding? = null
    private val binding:DialogDescriptionListBinding
    get() = _binding!!

    private lateinit var adapter:DescriptionListDialogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_description_list, container,false)
        setUpRecyclerView()
        setDialogBackground()
        return binding.root
    }

    private fun setUpRecyclerView(){
        adapter = DescriptionListDialogAdapter(descriptionList){
            callback(it)
            dismiss()
        }
     binding.rvDialogDescriptionList.adapter = adapter
    }


}