package ru.skillbranch.gameofthrones.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.ActivityRootBinding
import ru.skillbranch.gameofthrones.ui.splash.SplashFragmentDirections

class RootActivity : AppCompatActivity() {
    private lateinit var viewModel: RootViewModel
    lateinit var navController: NavController
    lateinit var binding:ActivityRootBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        savedInstanceState ?: prepareData()
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    private fun prepareData() {
        viewModel.syncDataIfNeed().observe(this, Observer<LoadResult<Boolean>> {
            when(it){
                is LoadResult.Loading -> {
                    navController.navigate(R.id.nav_splash)
                }
                is LoadResult.Success -> {
                    val action = SplashFragmentDirections.actionNavSplashToNavHouses()
                    navController.navigate(action)
                }
                is LoadResult.Error -> {
                    Snackbar.make(
                        binding.root,
                        it.errorMessage.toString(),
                        Snackbar.LENGTH_INDEFINITE
                    ).show()
                }
            }
        })
    }

    private fun initViewModel() {
        TODO("Not yet implemented")
    }
}