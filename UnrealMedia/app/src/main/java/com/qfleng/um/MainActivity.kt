package com.qfleng.um

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBar
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.qfleng.um.databinding.ActivityMainBinding
import com.qfleng.um.viewmodel.MainViewModel


/**
 * Created by Duke
 */
class MainActivity : BaseActivity() {


    companion object {
        init {
            System.loadLibrary("avcodec")
            System.loadLibrary("avutil")
            System.loadLibrary("swresample")
            System.loadLibrary("avfilter")
            System.loadLibrary("NativeMedia")
        }

    }

    private lateinit var navController: NavController


    private val mainViewModel by viewModels<MainViewModel>()
    val vBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(vBinding.root)

        setupnavView(vBinding.navView)


        PermissionsManager.requestAllNeedPermissions(this)

        mainViewModel.loadMedias(this)



        navController = findNavController(this, R.id.my_nav_host_fragment)
//        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                vBinding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
            R.id.action_refresh -> {
                mainViewModel.loadMedias(this, false)
            }


        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupnavView(navView: NavigationView) {

        setSupportActionBar(vBinding.toolbar)
        var ab: ActionBar? = supportActionBar
        if (null != ab) {
            ab.title = getString(R.string.app_name)
            ab.setHomeAsUpIndicator(R.drawable.ic_menu)
            ab.setDisplayHomeAsUpEnabled(true)

        }

        navView.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {


                vBinding.drawerLayout.closeDrawers()

                when (item.itemId) {
                    R.id.nav_about -> {
                        startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                    }
                    R.id.nav_sort_by_artist -> {
                        if (navController.currentDestination!!.id != R.id.artistSortFragment) {
                            navController.navigate(R.id.action_to_artistSortFragment)
                        }
                    }
                    R.id.nav_sort_all -> {
                        if (navController.currentDestination!!.id != R.id.mainFragment) {
                            navController.navigate(R.id.mainFragment)
                        }
                    }
                    R.id.nav_settings -> {
                        startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                    }
                }
                return true
            }
        })
    }

}