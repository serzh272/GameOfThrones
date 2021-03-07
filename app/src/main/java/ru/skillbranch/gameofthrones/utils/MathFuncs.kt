package ru.skillbranch.gameofthrones.utils

import kotlin.math.PI
import kotlin.math.sin

class MathFuncs {
    companion object{
        fun graf(x:Long, numBounces:Int, maxLength:Long):Int{
            val koeff = x.toDouble()/(maxLength.toDouble()/PI/20/(2*numBounces-1))
            return if (x > maxLength){
                255
            }else{
                ((127.5f - koeff/numBounces)* sin(koeff/20 - PI/2) + koeff/numBounces + 127.5f).toInt()
            }
        }
    }

}