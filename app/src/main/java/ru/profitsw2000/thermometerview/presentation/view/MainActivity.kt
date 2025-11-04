package ru.profitsw2000.thermometerview.presentation.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import ru.profitsw2000.navigator.Navigator
import ru.profitsw2000.thermometerview.R
import ru.profitsw2000.thermometerview.databinding.ActivityMainBinding
import ru.profitsw2000.thermometerview.navigation.NavigatorImpl

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragment_container_view
        ) as NavHostFragment

        navController = navHostFragment.navController
        loadKoinModules( module { single<Navigator> {NavigatorImpl(navController)} })
        binding.bottomNavigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.main, R.id.table, R.id.graph)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            if (destination.id == R.id.main ||
                destination.id == R.id.graph ||
                destination.id == R.id.table){
                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.bottomNavigationView.visibility = View.GONE
                actionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}