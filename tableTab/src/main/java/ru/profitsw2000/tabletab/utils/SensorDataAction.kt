package ru.profitsw2000.tabletab.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class SensorDataAction : Parcelable {
    @Parcelize
    data object SerialNumberDataAction: SensorDataAction()

    @Parcelize
    data object LocalIdDataAction: SensorDataAction()

    @Parcelize
    data object LetterCodeDataAction: SensorDataAction()
}