package ru.profitsw2000.tabletab.utils

sealed class SensorDataAction {
    data object SerialNumberDataAction: SensorDataAction()
    data object LocalIdDataAction: SensorDataAction()
    data object LetterCodeDataAction: SensorDataAction()
}