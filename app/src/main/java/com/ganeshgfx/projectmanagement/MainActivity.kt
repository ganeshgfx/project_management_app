package com.ganeshgfx.projectmanagement

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.databinding.ActivityMainBinding
import com.ganeshgfx.projectmanagement.di.AppContainer
import com.ganeshgfx.projectmanagement.di.ProjectContainer
import com.ganeshgfx.projectmanagement.di.TaskListContainer
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.viewModels.MainActivityViewModel
import com.google.android.material.elevation.SurfaceColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var appContainer: AppContainer

    private lateinit var binding: ActivityMainBinding

    lateinit var viewModel : MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MainApplication

        appContainer = app.appContainer

        appContainer.projectContainer = ProjectContainer(appContainer.projectRepository)
        appContainer.taskListContainer = TaskListContainer(appContainer.taskListRepository)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[MainActivityViewModel::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = this

        //setting color for appbar and statusbar
        val color = SurfaceColors.SURFACE_2.getColor(this)
        //window.statusBarColor = color
        //window.navigationBarColor = color

        //appbar setup
        //setSupportActionBar(binding.toolbar)
        //binding.toolbar.setBackgroundColor(color)
        //Navigation Setup

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        //val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.bottomNavigation.setupWithNavController(navController)
        //binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController,appBarConfiguration)
        navController.addOnDestinationChangedListener{_, destination, _ ->
            when(destination.id){
                R.id.projectFragment -> {
                    viewModel.hideBottomAppBar(true)
                    window.navigationBarColor = Color.TRANSPARENT
                }
                R.id.projectOverviewFragment -> {
                    viewModel.hideBottomAppBar(false)
                    window.navigationBarColor = color
                }
                R.id.tasksListsFragment -> {
                    viewModel.hideBottomAppBar(false)
                    window.navigationBarColor = color
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        appContainer.projectContainer = null
    }

}