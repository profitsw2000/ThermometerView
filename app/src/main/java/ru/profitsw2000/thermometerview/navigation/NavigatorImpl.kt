package ru.profitsw2000.thermometerview.navigation

import android.os.Bundle
import androidx.navigation.NavController
import ru.profitsw2000.core.R
import ru.profitsw2000.navigator.Navigator

class NavigatorImpl(private val navController: NavController) : Navigator {
    override fun navigateToSensorInfoBottomSheet(bundle: Bundle) {
        navController.navigate(ru.profitsw2000.thermometerview.R.id.action_main_to_sensor_info, bundle)
    }
}