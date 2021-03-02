package ru.skillbranch.gameofthrones.extensions

import android.content.Context
import android.net.ConnectivityManager

val Context.isNetworkAvailable:Boolean
    get() {
        val cm:ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info?.isConnectedOrConnecting == true
    }

fun Context.dpToPx(dp:Int):Float{
    return dp.toFloat() * this.resources.displayMetrics.density
}