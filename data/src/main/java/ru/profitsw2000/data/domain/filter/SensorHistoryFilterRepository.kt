package ru.profitsw2000.data.domain.filter

import java.util.Date

interface SensorHistoryFilterRepository {
    var fromDate: Date?
    var toDate: Date?
}