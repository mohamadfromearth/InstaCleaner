package com.example.instacleaner.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
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