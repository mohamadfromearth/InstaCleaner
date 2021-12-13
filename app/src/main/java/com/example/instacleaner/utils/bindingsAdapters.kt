package com.example.instacleaner.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.instacleaner.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import java.lang.StringBuilder

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context).load(url).into(view)
    }
}


@BindingAdapter("translateNumber")
fun translateNumbers(view:MaterialTextView,number:String){
    val result = StringBuilder()
    view.text = "-"

    number.apply {

     number.forEach {
         when(it){
             '0' -> result.append("۰")
             '1' -> result.append("۱")
             '2' -> result.append("۲")
             '3' -> result.append("۳")
             '4' -> result.append("۴")
             '5' -> result.append("۵")
             '6' -> result.append("۶")
             '7' -> result.append("۷")
             '8' -> result.append("۸")
             '9' -> result.append("۹")
         }
     }
        if (result.isNotEmpty())
              view.text = result
    }

}
