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
 * Creates and remembers a [TimePickerState].
 *
 * @param initialPeriodIndex Initial selected AM/PM index.
 * @param initialHourIndex Initial selected hour index.
 * @param initialMinuteIndex Initial selected minute index.
 * @param initialSecondIndex Initial selected second index.
 */
@ExperimentalSnapPickerApi
@Composable
@Stable
public fun rememberTimePickerState(
    initialPeriodIndex: Int = 0,
    initialHourIndex: Int = 0,
    initialMinuteIndex: Int = 0,
    initialSecondIndex: Int = 0
): TimePickerState {
    return rememberSaveable(saver = TimePickerState.Saver) {
        TimePickerState(
            initialPeriodIndex = initialPeriodIndex,
            initialHourIndex = initialHourIndex,
            initialMinuteIndex = initialMinuteIndex,
            initialSecondIndex = initialSecondIndex
        )
    }
}

/**
 * A state object of the time picker.
 *
 * @param initialPeriodIndex Initial selected AM/PM index.
 * @param initialHourIndex Initial selected hour index.
 * @param initialMinuteIndex Initial selected minute index.
 * @param initialSecondIndex Initial selected second index.
 */
@ExperimentalSnapPickerApi
@Stable
public class TimePickerState(
    initialPeriodIndex: Int = 0,
    initialHourIndex: Int = 0,
    initialMinuteIndex: Int = 0,
    initialSecondIndex: Int = 0
) {
    internal val periodPickerState = SnapPickerState(initialIndex = initialPeriodIndex)
    internal val hourPickerState = SnapPickerState(initialIndex = initialHourIndex)
    internal val minutePickerState = SnapPickerState(initialIndex = initialMinuteIndex)
    internal val secondPickerState = SnapPickerState(initialIndex = initialSecondIndex)

    private var _currentHour: Int by mutableStateOf(initialHourIndex)
    private var _currentMinute: Int by mutableStateOf(initialMinuteIndex)
    private var _currentSecond: Int by mutableStateOf(initialSecondIndex)

    /**
     * The current selected hour in the picker.
     * The hour value is returned as 24 hour format, from 0 to 23.
     * It may not updated instantly if scroll is on progress.
     */
    public var currentHour: Int
        get() = _currentHour
        internal set(value) {
            if (value != _currentHour) {
                _currentHour = value
            }
        }

    /**
     * The current selected minute in the picker.
     * The value is range from 0 to 59.
     * It may not updated instantly if scroll is on progress.
     */
    public var currentMinute: Int
        get() = _currentMinute
        internal set(value) {
            if (value != _currentMinute) {
                _currentMinute = value
            }
        }

    /**
     * The current selected second in the picker.
     * The value is range from 0 to 59.
     * It may not updated instantly if scroll is on progress.
     */
    public var currentSecond: Int
        get() = _currentSecond
        internal set(value) {
            if (value != _currentSecond) {
                _currentSecond = value
            }
        }

    /**
     * Whether this time picker is currently scrolling by gesture,
     * fling or programmatically or not.
     */
    public val isScrollInProgress: Boolean
        get() = periodPickerState.lazyListState.isScrollInProgress ||
            hourPickerState.lazyListState.isScrollInProgress ||
            minutePickerState.lazyListState.isScrollInProgress ||
            secondPickerState.lazyListState.isScrollInProgress

    public companion object {
        /**
         * The default saver for [TimePickerState].
         */
        public val Saver: Saver<TimePickerState, List<Int>> = Saver(
            save = {
                listOf(
                    it.periodPickerState.currentIndex,
                    it.hourPickerState.currentIndex,
                    it.minutePickerState.currentIndex,
                    it.secondPickerState.currentIndex
                )
            },
            restore = {
                TimePickerState(
                    initialPeriodIndex = it[0],
                    initialHourIndex = it[1],
                    initialMinuteIndex = it[2],
                    initialSecondIndex = it[3]
                )
            }
        )
    }
}
