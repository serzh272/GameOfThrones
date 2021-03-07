package ru.skillbranch.gameofthrones.ui.splash

import android.animation.ValueAnimator
import android.graphics.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentSplashBinding
import ru.skillbranch.gameofthrones.utils.MathFuncs

class SplashFragment : Fragment() {
    lateinit var binding:FragmentSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSplashBinding.inflate(layoutInflater)
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 5000
            addUpdateListener {
                var c = MathFuncs.graf(currentPlayTime, 4, 5000)
                val value = animatedValue as Float
                binding.splash.drawable.colorFilter = PorterDuffColorFilter(Color.rgb((255*value).toInt(), (c*value).toInt(), (c*value).toInt()), PorterDuff.Mode.MULTIPLY)
            }
        }.start()




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return binding.root
    }
}