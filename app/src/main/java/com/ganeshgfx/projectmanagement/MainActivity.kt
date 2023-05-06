package com.ganeshgfx.projectmanagement

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.ActivityMainBinding
import com.ganeshgfx.projectmanagement.viewModels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.isLogged.observe(this) {
            if (!it) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            else{
                viewModel.startRepo()
            }
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.data = viewModel
        binding.lifecycleOwner = this

        //val color = SurfaceColors.SURFACE_2.getColor(this)
        window.navigationBarColor = Color.TRANSPARENT


        //setSupportActionBar(binding.toolbar)
        //binding.toolbar.setBackgroundColor(color)
        //Navigation Setup

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        //val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.bottomNavigation.setupWithNavController(navController)
        //binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController,appBarConfiguration)
        navController.addOnDestinationChangedListener {  controller, destination, _ ->

            val backQueue = controller.backQueue.map {
                when(it.destination.id){
                    R.id.projectFragment -> "Project"
                    R.id.calenderFragment -> "Calender"
                    R.id.projectOverviewFragment -> "Overview"
                    R.id.tasksListsFragment -> "Tasks"
                    R.id.chatFragment -> "Chat"
                    R.id.manageProjectFragment -> "Manage"
                    R.id.addMemberFragment -> "Add Member"
                    else -> "Unknown"
                }
            }


            if(backQueue.size > 3 ){
                viewModel.hideBottomAppBar(true)
            }else{
                viewModel.hideBottomAppBar(false)
            }

            log("BackQueue: $backQueue")



            when (destination.id) {
                R.id.projectFragment -> {
                    viewModel.hideBottomAppBar(true)
//                    val typedValue = TypedValue()
//                    theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
//                    typedValue.data
                    window.navigationBarColor = Color.TRANSPARENT
                }

                R.id.calenderFragment -> {
                    //viewModel.hideBottomAppBar(false)
                    window.navigationBarColor = Color.TRANSPARENT
                }

                R.id.projectOverviewFragment -> {
                   // viewModel.hideBottomAppBar(false)
                    window.navigationBarColor = Color.TRANSPARENT
                }

                R.id.tasksListsFragment -> {
                   // viewModel.hideBottomAppBar(false)
                    window.navigationBarColor = Color.TRANSPARENT
                }
            }
        }
    }
}