package com.example.instacleaner.utils

import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.instacleaner.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import java.lang.StringBuilder

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context).asBitmap().load(url).into(view)
    }
}

@BindingAdapter("imageVisibility")
fun setVisibility(view:ImageView,visibility:Boolean){
    view.isVisible = visibility
}

