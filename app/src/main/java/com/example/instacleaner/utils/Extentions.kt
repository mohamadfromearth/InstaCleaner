package com.example.instacleaner.utils

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.instacleaner.App
import com.example.instacleaner.BuildConfig
import com.google.android.material.snackbar.Snackbar
import retrofit2.Response


fun<T> handleResponse( response: Response<T>):Resource<T> {


    if (response.isSuccessful){
        response.body()?.let {
            return Resource.Success(it)
        }

    }
    if (response.code() == 400){
        return Resource.Error("Bad request")
    }
    return Resource.Error(response.toString())
}



 fun extractCookie(cookie: String, cookieValue: String): String {
    val startPosition = cookie.indexOf(cookieValue) + cookieValue.length
    val endPosition = cookie.indexOf("%", startPosition)
    return cookie.substring(startPosition, endPosition)
}


fun Fragment.showToast(message:String){
    Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
}





fun Any?.translateNumber():String{
    val persianNumbers = "۰۱۲۳۴۵۶۷۸۹"
    val englishNumber = "0123456789"

    var result = this
    result?.let{
        englishNumber.forEachIndexed { index, c ->
            result = result.toString().replace(c,persianNumbers[index])
        }
        return result.toString()
    }?: kotlin.run {
        return ""
    }
}


fun Fragment.showSnackBar(message:String){
    Snackbar.make(requireView(),message,Snackbar.LENGTH_SHORT).show()
}



fun DialogFragment.setDialogBackground(){
    if (dialog != null && dialog?.window != null) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val width = (resources.displayMetrics.widthPixels * 0.20).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
        dialog?.window?.setLayout(width, height)
    }
}


fun log(text:String){
    if (BuildConfig.DEBUG){
        Log.d("Mohamad", text)
    }
}


fun ViewGroup.setChildTypeface(typeface: Typeface?) {
    for (i in 0 until childCount) {
        val view = getChildAt(i)
        if (view is TextView) {
            view.typeface = typeface
        }
    }
}

fun ViewGroup.setChildTextSize(size: Float) {
    for (i in 0 until childCount) {
        val view = getChildAt(i)
        if (view is TextView) {
            view.textSize = convertSPtoPX(size).toFloat()
        }
    }
}


fun dpToPx(dp:Float) =
    dp * App.getInstance().resources.displayMetrics.density



fun convertPXtoSP(px: Float): Float {
    return px / App.getInstance().resources.displayMetrics.scaledDensity
}

fun convertDPtoPX(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.getInstance().resources.displayMetrics)
}

fun convertPXtoDP(px: Float): Float {
    return px / (App.getInstance().resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun convertSPtoPX(sp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, App.getInstance().resources.displayMetrics)
        .toInt()
}

fun convertDPtoSP(dp: Float): Int {
    return (convertDPtoPX(dp) / App.getInstance().resources.displayMetrics.scaledDensity).toInt()
}