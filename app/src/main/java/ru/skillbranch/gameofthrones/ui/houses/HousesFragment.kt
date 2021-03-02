package ru.skillbranch.gameofthrones.ui.houses

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentHousesBinding
import ru.skillbranch.gameofthrones.ui.RootActivity
import kotlin.math.hypot
import kotlin.math.max

class HousesFragment : Fragment() {
    lateinit var binding: FragmentHousesBinding
    private lateinit var colors:Array<Int>
    private lateinit var housesPagerAdapter:HousesPagerAdapter

    @ColorInt
    private var currentColor:Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        housesPagerAdapter = HousesPagerAdapter(childFragmentManager)
        colors = requireContext().run {
            arrayOf(
                getColor(R.color.stark_primary),
                getColor(R.color.lannister_primary),
                getColor(R.color.targaryen_primary),
                getColor(R.color.baratheon_primary),
                getColor(R.color.greyjoy_primary),
                getColor(R.color.martel_primary),
                getColor(R.color.tyrel_primary)

            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        with(menu.findItem(R.id.action_search)?.actionView as SearchView){
            queryHint = "Search character"
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHousesBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as RootActivity).setSupportActionBar(binding.toolbar)
        if (currentColor != -1) binding.appbar.setBackgroundColor(currentColor)
        binding.viewPager.adapter = housesPagerAdapter
        with(binding.tabs){
            setupWithViewPager(binding.viewPager)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val position: Int = tab?.position ?: -1
                    if ((binding.appbar.solidColor ) != colors[position]) {
                        val rect = Rect()
                        val tabView = tab?.view as View
                        tabView.postDelayed(
                            {
                                tabView.getGlobalVisibleRect(rect)
                                animateAppBarReval(position, rect.centerX(), rect.centerY())
                            },
                            300
                        )
//                        tabView.getGlobalVisibleRect(rect)
//                        animateAppBarReval(position, rect.centerX(), rect.centerY())
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}

            })
        }
    }

    private fun animateAppBarReval(position: Int, centerX: Int, centerY: Int) {
        val endRadius = max(
            hypot(centerX.toDouble(), centerY.toDouble()),
            hypot(binding.appbar.width.toDouble() - centerX.toDouble(), centerY.toDouble())
        )
        with(binding.revealView){
            visibility = View.VISIBLE
            setBackgroundColor(colors[position])
        }
        ViewAnimationUtils.createCircularReveal(
            binding.revealView,
            centerX,
            centerY,
            0f,
            endRadius.toFloat()
        ).apply {
            doOnEnd {
                binding.appbar.setBackgroundColor(colors[position])
                binding.revealView.visibility = View.INVISIBLE
            }
            start()
        }
        currentColor = colors[position]
    }

}