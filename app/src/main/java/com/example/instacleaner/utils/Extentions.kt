package com.example.instacleaner.utils

import android.graphics.Typeface
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
    return Resource.Error("Something went wrong")
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