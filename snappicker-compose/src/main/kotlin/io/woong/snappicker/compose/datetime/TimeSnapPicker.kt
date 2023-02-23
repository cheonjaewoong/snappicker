package io.woong.snappicker.compose.datetime

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.woong.snappicker.compose.ExperimentalSnapPickerApi
import io.woong.snappicker.compose.VerticalSnapPicker
import io.woong.snappicker.datetime.TimeFormat
import io.woong.snappicker.datetime.TimePeriod

/**
 * The vertical scrollable time picker that allows user to select one time from time list.
 *
 * @param modifier The modifier to apply to this composable.
 * @param state The state object to manage this picker's state.
 * @param timeFormat The time format of this picker, 12 hour or 24 hour format.
 * @param hourEnabled Whether the hour picker is visible.
 * @param minuteEnabled Whether the minute picker is visible.
 * @param secondEnabled Whether the second picker is visible.
 * @param minuteInterval The interval of the minutes.
 * This value must be a divisor of 60 (1, 2, 3, 4, 5, 6, 10, 12, 15, 20 or 30). Default is 1.
 * @param secondInterval The interval of the seconds.
 * This value must be a divisor of 60 (1, 2, 3, 4, 5, 6, 10, 12, 15, 20 or 30). Default is 1.
 * @param itemHeight The height size of each item composable's container.
 * @param decorationBox Composable to add decoration around pickers, such as indicator or something.
 * The actual pickers will be passed to this lambda's parameter, "innerPickers".
 * You must call `innerPickers` to display pickers.
 * If it is not called, the pickers never visible.
 * @param periodItemContent The content composable of the time period (AM/PM) picker item.
 * @param hourItemContent The content composable of the hour picker item.
 * @param minuteItemContent The content composable of the minute picker item.
 * @param secondItemContent The content composable of the second picker item.
 */
@ExperimentalSnapPickerApi
@Composable
public fun VerticalTimeSnapPicker(
    modifier: Modifier = Modifier,
    state: TimeSnapPickerState = rememberTimeSnapPickerState(),
    timeFormat: TimeFormat = TimeFormat.Format24Hour,
    hourEnabled: Boolean = true,
    minuteEnabled: Boolean = true,
    secondEnabled: Boolean = true,
    minuteInterval: Int = 1,
    secondInterval: Int = 1,
    itemHeight: Dp = 48.dp,
    decorationBox: @Composable BoxScope.(innerPickers: @Composable () -> Unit) -> Unit =
        @Composable { innerPickers -> innerPickers() },
    periodItemContent: @Composable BoxScope.(period: TimePeriod) -> Unit,
    hourItemContent: @Composable BoxScope.(hour: Int) -> Unit,
    minuteItemContent: @Composable BoxScope.(minute: Int) -> Unit,
    secondItemContent: @Composable BoxScope.(second: Int) -> Unit,
) {
    val amPm = remember { listOf(TimePeriod.Am, TimePeriod.Pm) }
    val hours = remember {
        when (timeFormat) {
            TimeFormat.Format12Hour -> (1..12).toList()
            TimeFormat.Format24Hour -> (0..23).toList()
        }
    }
    val minutes = remember { (0..59 step minuteInterval).toList() }
    val seconds = remember { (0..59 step secondInterval).toList() }

    // Update current time when visible items are changed.
    LaunchedEffect(
        state.periodPickerState.currentIndex,
        state.hourPickerState.currentIndex,
        state.minutePickerState.currentIndex,
        state.secondPickerState.currentIndex
    ) {
        when (timeFormat) {
            TimeFormat.Format12Hour -> {
                snapshotFlow {
                    listOf(
                        state.periodPickerState,
                        state.hourPickerState,
                        state.minutePickerState,
                        state.secondPickerState
                    )
                }.collect {
                    val periodIndex = it[0].currentIndex
                    val hourIndex = it[1].currentIndex
                    val minuteIndex = it[2].currentIndex
                    val secondIndex = it[3].currentIndex
                    val amOrPm = amPm[periodIndex]
                    val hour = hours[hourIndex]
                    state.currentHour = when (amOrPm) {
                        TimePeriod.Am -> {
                            if (hour != 12) hour else 0
                        }
                        TimePeriod.Pm -> {
                            if (hour != 12) hour + 12 else 12
                        }
                    }
                    state.currentMinute = minutes[minuteIndex]
                    state.currentSecond = seconds[secondIndex]
                }
            }
            TimeFormat.Format24Hour -> {
                snapshotFlow {
                    listOf(
                        state.hourPickerState,
                        state.minutePickerState,
                        state.secondPickerState
                    )
                }.collect {
                    val hourIndex = it[0].currentIndex
                    val minuteIndex = it[1].currentIndex
                    val secondIndex = it[2].currentIndex
                    state.currentHour = hours[hourIndex]
                    state.currentMinute = minutes[minuteIndex]
                    state.currentSecond = seconds[secondIndex]
                }
            }
        }
    }

    BoxWithConstraints(modifier) {
        decorationBox {
            Row(modifier = Modifier.size(maxWidth, maxHeight)) {
                if (timeFormat == TimeFormat.Format12Hour && hourEnabled) {
                    VerticalSnapPicker(
                        values = amPm,
                        state = state.periodPickerState,
                        modifier = Modifier.weight(1f),
                        repeated = false,
                        itemHeight = itemHeight,
                        itemContent = periodItemContent
                    )
                }
                if (hourEnabled) {
                    VerticalSnapPicker(
                        values = hours,
                        state = state.hourPickerState,
                        modifier = Modifier.weight(1f),
                        itemHeight = itemHeight,
                        itemContent = hourItemContent
                    )
                }
                if (minuteEnabled) {
                    VerticalSnapPicker(
                        values = minutes,
                        state = state.minutePickerState,
                        modifier = Modifier.weight(1f),
                        itemHeight = itemHeight,
                        itemContent = minuteItemContent
                    )
                }
                if (secondEnabled) {
                    VerticalSnapPicker(
                        values = seconds,
                        state = state.secondPickerState,
                        modifier = Modifier.weight(1f),
                        itemHeight = itemHeight,
                        itemContent = secondItemContent
                    )
                }
            }
        }
    }
}
