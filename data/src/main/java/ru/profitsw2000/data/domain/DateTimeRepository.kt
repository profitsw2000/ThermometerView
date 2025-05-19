package ru.profitsw2000.data.domain

import kotlinx.coroutines.flow.StateFlow

interface DateTimeRepository {

    val dateDataString: StateFlow<String>
    val timeDataString: StateFlow<String>

    fun getCurrentDateTimeArray(): Array<Int>
}