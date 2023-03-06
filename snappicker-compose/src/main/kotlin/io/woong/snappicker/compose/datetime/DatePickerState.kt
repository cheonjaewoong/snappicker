package io.woong.snappicker.compose.datetime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import io.woong.snappicker.ExperimentalSnapPickerApi
import io.woong.snappicker.compose.SnapPickerState

/**
 * Creates and remembers a [DatePickerState].
 *
 * @param initialYearIndex Initial selected year index.
 * @param initialMonthIndex Initial selected month index.
 * @param initialDateIndex Initial selected date index.
 */
@ExperimentalSnapPickerApi
@Composable
@Stable
public fun rememberDatePickerState(
    initialYearIndex: Int = 2022,
    initialMonthIndex: Int = 0,
    initialDateIndex: Int = 0
): DatePickerState {
    return rememberSaveable(saver = DatePickerState.Saver) {
        DatePickerState(
            initialYearIndex = initialYearIndex,
            initialMonthIndex = initialMonthIndex,
            initialDateIndex = initialDateIndex
        )
    }
}

/**
 * A state object of the date picker.
 *
 * @param initialYearIndex Initial selected year index.
 * @param initialMonthIndex Initial selected month index.
 * @param initialDateIndex Initial selected date index.
 */
@ExperimentalSnapPickerApi
@Stable
public class DatePickerState(
    initialYearIndex: Int = 2022,
    initialMonthIndex: Int = 0,
    initialDateIndex: Int = 0
) {
    internal val yearPickerState = SnapPickerState(initialIndex = initialYearIndex)
    internal val monthPickerState = SnapPickerState(initialIndex = initialMonthIndex)
    internal var datePickerState = SnapPickerState(initialIndex = initialDateIndex)

    private var _currentYear: Int by mutableStateOf(initialYearIndex)
    private var _currentMonth: Int by mutableStateOf(initialMonthIndex + 1)
    private var _currentDate: Int by mutableStateOf(initialDateIndex + 1)

    /**
     * The current selected year in the picker.
     * It may not updated instantly if scroll is on progress.
     */
    public var currentYear: Int
        get() = _currentYear
        internal set(value) {
            if (value != _currentYear) {
                _currentYear = value
            }
        }

    /**
     * The current selected month in the picker.
     * The value is range from 1 to 12.
     * It may not updated instantly if scroll is on progress.
     */
    public var currentMonth: Int
        get() = _currentMonth
        internal set(value) {
            if (value != _currentMonth) {
                _currentMonth = value
            }
        }

    /**
     * The current selected date in the picker.
     * Possible value range is changed by year and month.
     * It may not updated instantly if scroll is on progress.
     */
    public var currentDate: Int
        get() = _currentDate
        internal set(value) {
            if (value != _currentDate) {
                _currentDate = value
            }
        }

    /**
     * Whether this time picker is currently scrolling by gesture,
     * fling or programmatically or not.
     */
    public val isScrollInProgress: Boolean
        get() = yearPickerState.lazyListState.isScrollInProgress ||
            monthPickerState.lazyListState.isScrollInProgress ||
            datePickerState.lazyListState.isScrollInProgress

    public companion object {
        /**
         * The default saver for [DatePickerState].
         */
        public val Saver: Saver<DatePickerState, List<Int>> = Saver(
            save = {
                listOf(
                    it.yearPickerState.currentIndex,
                    it.monthPickerState.currentIndex,
                    it.datePickerState.currentIndex
                )
            },
            restore = {
                DatePickerState(
                    initialYearIndex = it[0],
                    initialMonthIndex = it[1],
                    initialDateIndex = it[2]
                )
            }
        )
    }
}
