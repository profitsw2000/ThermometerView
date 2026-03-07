package ru.profitsw2000.data.enumer

sealed class TimeFrameDataObtainingMethod {
    data object TimeFrameBegin: TimeFrameDataObtainingMethod()
    data object TimeFrameEnd: TimeFrameDataObtainingMethod()
    data object TimeFrameMaximum: TimeFrameDataObtainingMethod()
    data object TimeFrameMinimum: TimeFrameDataObtainingMethod()
    data object TimeFrameAverage: TimeFrameDataObtainingMethod()
}