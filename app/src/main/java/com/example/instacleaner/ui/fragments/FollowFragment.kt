package com.example.instacleaner.ui.fragments


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.Gravity.NO_GRAVITY
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.instacleaner.App
import com.example.instacleaner.R
import com.example.instacleaner.adapters.FollowAdapter
import com.example.instacleaner.databinding.FragmentFollowBinding
import com.example.instacleaner.ui.dialog.MainDialog
import com.example.instacleaner.ui.viewModels.FollowViewModel
import com.example.instacleaner.utils.log
import com.example.instacleaner.utils.setChildTypeface
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.appcompat.view.menu.MenuPopupHelper

import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.Job


@SuppressLint("RestrictedApi")
@AndroidEntryPoint
class FollowFragment : Fragment(R.layout.fragment_follow),
    MenuBuilder.Callback {

    private lateinit var adapter: FollowAdapter

    private val viewModel:FollowViewModel by viewModels()

    private var _binding: FragmentFollowBinding? = null
    private val binding: FragmentFollowBinding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DataBindingUtil.bind(view)!!
        binding.viewModel = viewModel
        setUpRecyclerView()
        init()
        setUpTabView()
        subscribeToObservers()
        binding.options.setOnClickListener {
            showPopUpMenu(it)
        }

        binding.edtSearch.addTextChangedListener {
            viewModel.search(binding.edtSearch.text.toString())
        }
    }


    private fun init(){
        binding.rvFollow.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(recyclerView.computeVerticalScrollExtent() +
                recyclerView.computeVerticalScrollOffset() >
                recyclerView.computeVerticalScrollRange() - 100
                    ){
                    viewModel.paginate()
                }
            }
        })
        viewModel.onStart()

        binding.btnFilter.setOnClickListener {
            viewModel.btnFilterAction()
        }
    }

    private fun subscribeToObservers(){
        viewModel.adapterList.observe(viewLifecycleOwner,{
            adapter.submitList(it.toList()) {
                      if (viewModel.shouldScroll){
                          binding.rvFollow.scrollToPosition(0)
                          viewModel.shouldScroll = false
                      }
            }
        })

        viewModel.showFilterDialog.observe(viewLifecycleOwner,{

            MainDialog(it){ dialogModel ->
                viewModel.setFilter(dialogModel.filter)

            }.show(childFragmentManager,"")
        })

    }

    private fun setUpRecyclerView() {
        adapter = FollowAdapter(){ pos,user ->
             viewModel.onItemClickAction(pos,user)
        }
        binding.rvFollow.adapter = adapter
    }



    @SuppressLint("RestrictedApi")
    private fun showPopUpMenu(v:View){
        val wrapper = ContextThemeWrapper(requireContext(),R.style.AppTheme_TextAppearance_Popup)
        val popupMenu  = PopupMenu(wrapper,v)
        popupMenu.inflate(R.menu.follow_menu)

        val menuBuilder = MenuBuilder(requireContext())

        menuBuilder.setCallback(this)
        requireActivity().menuInflater.inflate(R.menu.follow_menu,menuBuilder)
        val menuHelper = MenuPopupHelper(
            requireContext(),
            menuBuilder ,v,false,0,R.style.BasePopupMenu
        )
        menuHelper.gravity = NO_GRAVITY

        menuHelper.setForceShowIcon(true)
        menuHelper.show()

    }


    private fun setUpTabView() {
        val tp = ResourcesCompat.getFont(requireContext(), R.font.iran_sans_web_medium)
        binding.followTab.getTabAt(0)?.view?.setChildTypeface(tp)
        binding.followTab.getTabAt(1)?.view?.setChildTypeface(tp)
        val currentTab = binding.followTab.getTabAt(viewModel.tabIndex)
        binding.followTab.selectTab(currentTab)
        binding.followTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                log("tabSelect ${tab?.position}")
                tab?.let {
                    viewModel.tabSelectAction(it.position)

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

    }



    override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
        viewModel.popUpMenuAction(item.itemId)
       return false
    }

    override fun onMenuModeChange(menu: MenuBuilder) {

    }


}