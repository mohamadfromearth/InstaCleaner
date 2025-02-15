package com.example.instacleaner.ui.dialog

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.instacleaner.R
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.databinding.DialogImageViewBinding
import com.example.instacleaner.utils.log
import com.example.instacleaner.utils.setDialogBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import util.extension.getInternalDirectory
import util.extension.shortenNumber
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class ImageDialog(private val onArrowRightClick:()->Unit,
                  private val onArrowLeftClick:()->Unit,
                  private val onSaveImage:(url:String) -> Unit,

):DialogFragment() {
    private var _binding:DialogImageViewBinding? = null
    private val binding:DialogImageViewBinding
    get() = _binding!!






    @Inject
    lateinit var downloadManager:DownloadManager

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var writePermissionGranted = false
    private var isCaption = false
    private lateinit var user:User
    private var title = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_image_view,container,false)
        setDialogBackground()
        binding.user = user
        binding.tvImageViewerCount.text = title

        val slideOutLeft = AnimationUtils.loadAnimation(requireContext(),R.anim.anim_slide_out_left)
        val slideInRight = AnimationUtils.loadAnimation(requireContext(),R.anim.anim_slide_in_right)
        val slideOutRight = AnimationUtils.loadAnimation(requireContext(),R.anim.anim_slide_out_right)
        val slideInLeft = AnimationUtils.loadAnimation(requireContext(),R.anim.anim_slide_in_left)
        val animShow = ScaleAnimation(0f, 1f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 200
            fillBefore = true
            fillAfter = true
        }
        val animHide = ScaleAnimation(1f, 0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 200
            fillBefore = true
            fillAfter = true
        }
        fadeOutTvImageViewerCount()
        //setData()
        slideOutLeft.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                binding.ivArrowLeft.isVisible = true
                fadeInTvImageView()
               // setData()
                binding.tvImageViewerCount.visibility = View.VISIBLE
                binding.clImageDialog.startAnimation(slideInRight)
            }
            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        slideOutRight.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                fadeInTvImageView()
                binding.clImageDialog.startAnimation(slideInLeft)
            /*   fadeInTvImageView()
                //if (currentUserPos == 0) binding.ivArrowLeft.isVisible = false
                setData()
                binding.clImageDialog.startAnimation(slideInLeft)
                binding.tvImageViewerCount.visibility = View.VISIBLE*/
            }
            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        slideInLeft.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                fadeOutTvImageViewerCount()
              //  setData()

            }
            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        slideInRight.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                fadeOutTvImageViewerCount()
               // setData()

            }
            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
        animHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                binding.dialogImageLinear.clearAnimation()
                binding.ivImageDialog.clearAnimation()
                binding.dialogImageLinear.visibility = if (isCaption) View.VISIBLE else View.INVISIBLE
                binding.ivImageDialog.visibility = if (isCaption) View.INVISIBLE else View.VISIBLE
                binding.tvFollowerCount.visibility = if (isCaption) View.VISIBLE else View.INVISIBLE
                binding.tvFollowingCount.visibility = if (isCaption) View.VISIBLE else View.INVISIBLE
                binding.ivToggle.setIconResource(if (isCaption) R.drawable.ic_image else R.drawable.ic_comment)
                val view = if (isCaption)binding.dialogImageLinear else binding.ivImageDialog
                view.startAnimation(animShow)

            }
            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
        animShow.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                binding.dialogImageLinear.clearAnimation()
                binding.ivImageDialog.clearAnimation()

            }
            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        binding.ivArrowRight.setOnClickListener {
            binding.tvImageViewerCount.visibility = View.VISIBLE
            binding.clImageDialog.startAnimation(slideOutLeft)
            binding.ivImageDialog.setImageResource(R.drawable.bg_primary_light)
            onArrowRightClick()
        }
        binding.ivArrowLeft.setOnClickListener {
            binding.tvImageViewerCount.visibility = View.VISIBLE
            binding.clImageDialog.startAnimation(slideOutRight)
            onArrowLeftClick()
        }
        binding.tvSaveImage.setOnClickListener {
            updateOrRequestPermission()
            val text = binding.tvSaveImage.text.toString()
            if (writePermissionGranted && text != getString(R.string.image_saved) && text != getString(R.string.copy)){
                user.hd_profile_pic_url_info?.let {
                    onSaveImage(it.url)
                }

            }

        }
        binding.ivToggle.setOnClickListener {

            isCaption = isCaption.not()
            val tvSaveText = if (user.isDownloaded) getString(R.string.image_saved) else getString(R.string.save_image)
            val view = if (isCaption) {
                binding.tvSaveImage.text = getString(R.string.copy)
                binding.ivImageDialog
            } else {
                binding.tvSaveImage.text = tvSaveText
                binding.dialogImageLinear
            }

            view.startAnimation(animHide)
        }
        binding.clImageDialog.setOnClickListener {
            fadeInTvImageView()
            fadeOutTvImageViewerCount()

        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
         writePermissionGranted = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted
            if (writePermissionGranted){
                user.hd_profile_pic_url_info?.let {
                    onSaveImage(it.url)
                }

            }
        }
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setData(user:User,pos:Int,size:Int){
        this.user = user
        lifecycleScope.launchWhenStarted {
            delay(150)
            binding.user = user
            binding.tvImageViewerCount.text = "${pos + 1} / $size"
            user.follower_count?.let{
                binding.tvFollowerCount.text = it.toString()
            }
            user.following_count?.let {
                binding.tvFollowingCount.text = it.toString()
            }

            if (user.isDownloaded){
                binding.tvSaveImage.text = getString(R.string.image_saved)
                binding.materialCardView.background = requireContext().getDrawable(R.drawable.bg_primary)
            }else{
                binding.tvSaveImage.text = getString(R.string.save_image)
                binding.materialCardView.background = requireContext().getDrawable(R.drawable.bg_primary_light)
            }
            if (isCaption){
                binding.ivToggle.setIconResource(R.drawable.ic_image)
                binding.tvSaveImage.text = getString(R.string.copy)

            }else{
                binding.ivToggle.setIconResource(R.drawable.ic_comment)
            }
        }

   }
    private fun fadeOutTvImageViewerCount(){
        binding.tvImageViewerCount.animate().scaleX(0.0F).scaleY(0.0F).alpha(0F).setDuration(750).setStartDelay(500).withEndAction {

        }.start()

    }
    private fun fadeInTvImageView(){
        binding.tvImageViewerCount.animate().scaleX(1F).scaleY(1F).alpha(1F).setDuration(0).setStartDelay(0).start()
    }
   /* private fun downloadImage(uri: String){
        val request = DownloadManager.Request(Uri.parse(uri))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,File.separator+"profiles"+ File.separator + File.separator+pair.second[currentUserPos].username )
            .setAllowedOverMetered(true)
        val downloadId = downloadManager.enqueue(request)
        val broadCast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)
                if (id == downloadId) {
                    setData()
                }
            }

        }
        requireActivity().registerReceiver(broadCast, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }*/
    private fun updateOrRequestPermission(){
        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        writePermissionGranted = hasWritePermission || minSdk29
        val permissionsToRequest = mutableListOf<String>()
        if(writePermissionGranted.not()) {
            permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }

    }
    fun initData(user: User,title:String){
        this.user = user
        this.title = title
    }









}