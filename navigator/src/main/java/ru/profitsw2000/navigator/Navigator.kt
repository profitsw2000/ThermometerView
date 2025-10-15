package ru.profitsw2000.navigator

import android.os.Bundle

interface Navigator {
    fun navigateToSensorInfoBottomSheet(bundle: Bundle)

    fun navigateToThermometerMemoryControlFragment()

    fun navigateUp()
}