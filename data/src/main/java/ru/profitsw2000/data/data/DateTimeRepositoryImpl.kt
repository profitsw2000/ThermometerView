package ru.profitsw2000.data.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.profitsw2000.data.domain.DateTimeRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class DateTimeRepositoryImpl : DateTimeRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val mutableDateDataString: MutableStateFlow<String> = MutableStateFlow(getCurrentDateString())
    override val dateDataString: StateFlow<String>
        get() = mutableDateDataString
    private val mutableTimeDataString: MutableStateFlow<String> = MutableStateFlow(getCurrentTimeString())
    override val timeDataString: StateFlow<String>
        get() = mutableTimeDataString

    init {
        startDateTimeFlow()
    }

    override fun getCurrentDateTimeArray(): Array<Int> {
        val calendar = Calendar.getInstance()
        val dayOfWeek =
            if (calendar.get(Calendar.DAY_OF_WEEK) - 1 < 1) 7
            else calendar.get(Calendar.DAY_OF_WEEK) - 1

        return arrayOf(
            calendar.get(Calendar.SECOND),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.HOUR_OF_DAY),
            dayOfWeek,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)%100
        )
    }

    private fun startDateTimeFlow() {
        coroutineScope.launch {
            while (isActive) {
                mutableDateDataString.value = getCurrentDateString()
                mutableTimeDataString.value = getCurrentTimeString()
                delay(500)
            }
        }
    }

    private fun getCurrentDateString(): String {
        val formatter = SimpleDateFormat("dd.MM.yy")
        val date = Date()
        return "${formatter.format(date)}"
    }

    private fun getCurrentTimeString(): String {
        val formatter = SimpleDateFormat("HH:mm:ss")
        val time = Date()
        return formatter.format(time)
    }

    private fun getDayOfWeek(): String {
        return when(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            1 -> "Вс"
            2 -> "Пн"
            3 -> "Вт"
            4 -> "Ср"
            5 -> "Чт"
            6 -> "Пт"
            7 -> "Сб"
            else -> "Пн"
        }
    }
}