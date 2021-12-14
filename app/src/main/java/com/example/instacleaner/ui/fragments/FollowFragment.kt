package com.example.instacleaner.ui.fragments


import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.instacleaner.App
import com.example.instacleaner.R
import com.example.instacleaner.adapters.FollowAdapter
import com.example.instacleaner.databinding.FragmentFollowBinding
import com.example.instacleaner.ui.viewModels.FollowViewModel
import com.example.instacleaner.utils.log
import com.example.instacleaner.utils.setChildTypeface
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowFragment : Fragment(R.layout.fragment_follow) {

    private lateinit var adapter: FollowAdapter

    private val viewModel:FollowViewModel by viewModels()


    @Inject
    lateinit var app: App

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

    }


    private fun setUpRecyclerView() {
        adapter = FollowAdapter(){

        }
        binding.rvFollow.adapter = adapter
    }


    private fun setUpTabView() {
        val tp = ResourcesCompat.getFont(requireContext(), R.font.iran_sans_web_medium)
        binding.followTab.getTabAt(0)?.view?.setChildTypeface(tp)
        binding.followTab.getTabAt(1)?.view?.setChildTypeface(tp)
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







}