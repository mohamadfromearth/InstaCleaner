package com.example.instacleaner.ui.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.instacleaner.R
import com.example.instacleaner.adapters.FollowAdapter
import com.example.instacleaner.databinding.FragmentFollowBinding
import com.example.instacleaner.ui.viewModels.FollowViewModel
import com.example.instacleaner.utils.log
import com.example.instacleaner.utils.setChildTypeface
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.view.menu.MenuBuilder
import androidx.navigation.fragment.findNavController
import com.example.instacleaner.data.local.ListDialogModel
import com.example.instacleaner.ui.dialog.*
import com.example.instacleaner.utils.translateNumber
import util.extension.snack


@SuppressLint("RestrictedApi")
@AndroidEntryPoint
class FollowFragment : Fragment(R.layout.fragment_follow),
    MenuBuilder.Callback {
    private lateinit var adapter: FollowAdapter
    private val viewModel:FollowViewModel by viewModels()
    private var isSelectFirstTime = true
    private lateinit var imageDialog: ImageDialog
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
        viewModel.onStart()
        binding.btnFilter.setOnClickListener {
            viewModel.btnFilterAction()
        }
        binding.options.setOnClickListener {
            showPopUpMenu(it,R.menu.follow_menu)
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(binding.edtSearch.text.toString()) }
            override fun afterTextChanged(s: Editable?){}
        })
        binding.btnSort.setOnClickListener {
            viewModel.btnSortClick()
        }
        binding.btnSelection.setOnClickListener {
            viewModel.btnSelectionAction()
        }
        binding.btnPaginateState.setOnClickListener {
            viewModel.btnPaginateStateAction()
        }
        imageDialog = ImageDialog(
            onArrowRightClick = {
            viewModel.arrowRightAction()
            },
            onArrowLeftClick = {
            viewModel.arrowLeftAction()
            },
            onSaveImage = {
             viewModel.downloadImageAction(it)
            },

        )
    }
    private fun subscribeToObservers(){
        viewModel.adapterList.observe(viewLifecycleOwner,{
            if (it.isEmpty()){
                adapter.submitList(null)
            }else{
                adapter.submitList(it.toList()) {
                    if (viewModel.shouldScroll){
                        binding.rvFollow.scrollToPosition(0)
                        viewModel.shouldScroll = false
                    }
                }
            }

        })

        viewModel.showFilterDialog.observe(viewLifecycleOwner,{
            TabDialog(it){ dialogModel ->
                viewModel.setFilter(dialogModel.option)
            }.show(childFragmentManager,"")
        })

        viewModel.showSortDialog.observe(viewLifecycleOwner,{

            TabDialog(it){ dialogModel ->
                viewModel.setSort(dialogModel.option)
            }.show(childFragmentManager,"")
        })

        viewModel.showOptionDialog.observe(viewLifecycleOwner,{ pair ->
            ListDialog(pair){
                viewModel.itemOptionAction(it)
            }.show(parentFragmentManager,"")
        })

        viewModel.showDescriptionDialog.observe(viewLifecycleOwner,{
            DescriptionDialog(it){ option ->
                 viewModel.setPaginateState(option)
            }.show(parentFragmentManager,"")
        })

        viewModel.showBottomSheet.observe(viewLifecycleOwner,{
            BottomSheet(it){  options ->

                viewModel.bottomSheetAction(options)
            }.show(parentFragmentManager,"")
        })

        viewModel.showSnackBar.observe(viewLifecycleOwner,{
            snack(requireView(),it)
        })

        viewModel.selectionCount.observe(viewLifecycleOwner,{
            binding.btnSelection.text = it.translateNumber()
            if (it>0){
                if (isSelectFirstTime){
                    binding.btnSelection.visibility = View.VISIBLE
                    binding.btnSelection.animate().scaleX(1F).scaleY(1F).alpha(1F).setDuration(300).setStartDelay(0).start()
                    isSelectFirstTime = false
                }
            }else{
                if (!isSelectFirstTime){
                    binding.btnSelection.animate().scaleX(0.0F).scaleY(0.0F).alpha(0F).setDuration(300).setStartDelay(0).withEndAction {
                        binding.btnSelection.visibility = View.GONE
                    }.start()
                    isSelectFirstTime = true
                }

            }


        })

        viewModel.btnPaginateSateIcon.observe(viewLifecycleOwner,{
            binding.btnPaginateState.setIconResource(it)
        })

        viewModel.navToProfileFragment.observe(viewLifecycleOwner,{
            findNavController().navigate(FollowFragmentDirections.actionFollowFragment2ToProfileFragment())
        })

        viewModel.showImageViewDialog.observe(viewLifecycleOwner,{
            imageDialog.initData(it.first,"${it.third + 1}/${it.second}")
            imageDialog.show(parentFragmentManager,"")

        })

        viewModel.imageDialogData.observe(viewLifecycleOwner,{
            imageDialog.setData(it.first,it.second,it.third)
        })


    }
    private fun setUpRecyclerView() {
        adapter = FollowAdapter({ pos,user ->
            viewModel.itemClickAction(pos,user)
        },{pos, user ->
            viewModel.itemListOptionAction(pos,user)
        }){ pos,user ->
           viewModel.itemLongClickAction(pos,user)
        }
        binding.rvFollow.setHasFixedSize(true)
        binding.rvFollow.adapter = adapter
    }
    @SuppressLint("RestrictedApi")
    private fun showPopUpMenu(v:View,layoutId:Int){
        val wrapper = ContextThemeWrapper(requireContext(),R.style.AppTheme_TextAppearance_Popup)
        val itemWrapper = ContextThemeWrapper(requireContext(),R.style.BasePopupMenu)
        val popupMenu  = PopupMenu(wrapper,v)
        popupMenu.inflate(layoutId)

        val menuBuilder = MenuBuilder(wrapper)

        menuBuilder.setCallback(this)
        requireActivity().menuInflater.inflate(layoutId,menuBuilder)
        val menuHelper = MenuPopupHelper(
            itemWrapper,
            menuBuilder ,v,false,0,R.style.BasePopupMenu
        )
//        menuHelper.gravity = Gravity.RIGHT

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