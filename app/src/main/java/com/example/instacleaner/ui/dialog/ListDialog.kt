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
import com.example.instacleaner.adapters.ListDialogAdapter
import com.example.instacleaner.data.local.ListDialogModel
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.databinding.DialogListBinding


class ListDialog(private val  options:Pair<List<ListDialogModel>, User>):DialogFragment() {
    private var  _binding:DialogListBinding? = null
    private val binding:DialogListBinding
    get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_list, container,false)
        setUpRecyclerView()
        setDialogBackground()
        binding.title.text = options.second.username
        return binding.root
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


    private fun setUpRecyclerView(){
        binding.rvDialogList.adapter = ListDialogAdapter(options.first)
    }


}