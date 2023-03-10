package io.woong.snappicker.compose.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.woong.snappicker.ExperimentalSnapPickerApi
import io.woong.snappicker.compose.VerticalSnapPicker
import io.woong.snappicker.compose.calculateRepeatedLazyListMidIndex

/**
 * A date picker that allows user to select one date.
 *
 * @param modifier The modifier to apply to this composable.
 * @param state The state object to manage this picker's state.
 * @param yearEnabled Whether the year picker is visible.
 * @param monthEnabled Whether the month picker is visible.
 * @param dateEnabled Whether the date picker is visible.
 * @param itemHeight The height size of each item composable's container.
 * @param pickerSpacing The spacing between each picker.
 * @param contentPadding Padding around the pickers. This will be applied after [decorationBox].
 * In other word, [decorationBox] is not affected by this padding value.
 * @param decorationBox Composable to add decoration around pickers, such as indicator or something.
 * The actual pickers will be passed to this lambda's parameter, "innerPickers".
 * You must call `innerPickers` to display pickers.
 * If it is not called, the pickers never visible.
 * @param yearItemContent The content composable of the year picker item.
 * @param monthItemContent The content composable of the month picker item.
 * @param dateItemContent The content composable of the date picker item.
 */
@ExperimentalSnapPickerApi
@Composable
public fun VerticalDateSnapPicker(
    modifier: Modifier = Modifier,
    state: DateSnapPickerState = rememberDateSnapPickerState(),
    yearEnabled: Boolean = true,
    monthEnabled: Boolean = true,
    dateEnabled: Boolean = true,
    itemHeight: Dp = 48.dp,
    pickerSpacing: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    decorationBox: @Composable BoxScope.(innerPickers: @Composable () -> Unit) -> Unit =
        @Composable { innerPickers -> innerPickers() },
    yearItemContent: @Composable BoxScope.(year: Int) -> Unit,
    monthItemContent: @Composable BoxScope.(month: Int) -> Unit,
    dateItemContent: @Composable BoxScope.(date: Int) -> Unit
) {
    val years = remember { (1..9999).toList() }
    val months = remember { (1..12).toList() }
    val dates = remember { (1..31).toList() }

    // Scroll to possible last date if current date is impossible.
    LaunchedEffect(state.isScrollInProgress) {
        if (!state.isScrollInProgress) {
            val currentDateIndex = state.datePickerState.currentIndex
            val lastDateOfMonth = calculateLastDate(state.currentYear, state.currentMonth)
            if (currentDateIndex + 1 > lastDateOfMonth) {
                state.datePickerState.animateScrollToItem(calculateRepeatedLazyListMidIndex(
                    index = lastDateOfMonth - 1,
                    valuesCount = dates.size
                ))
            }
        }
    }

    // Update current date when picker positions are changed.
    LaunchedEffect(
        state.yearPickerState.currentIndex,
        state.monthPickerState.currentIndex,
        state.datePickerState.currentIndex
    ) {
        snapshotFlow {
            listOf(
                state.yearPickerState,
                state.monthPickerState,
                state.datePickerState
            )
        }.collect {
            val yearIndex = it[0].currentIndex
            val monthIndex = it[1].currentIndex
            val dateIndex = it[2].currentIndex
            state.currentYear = years[yearIndex]
            state.currentMonth = months[monthIndex]
            val lastDateOfMonth = calculateLastDate(state.currentYear, state.currentMonth)
            // Prevent impossible date.
            state.currentDate = dates[dateIndex].coerceIn(dates.first(), lastDateOfMonth)
        }
    }

    BoxWithConstraints(modifier) {
        decorationBox {
            Row(
                modifier = Modifier.size(maxWidth, maxHeight).padding(contentPadding),
                horizontalArrangement = Arrangement.spacedBy(pickerSpacing)
            ) {
                if (yearEnabled) {
                    VerticalSnapPicker(
                        values = years,
                        state = state.yearPickerState,
                        modifier = Modifier.weight(1f),
                        repeated = true,
                        itemHeight = itemHeight,
                        itemContent = yearItemContent
                    )
                }
                if (monthEnabled) {
                    VerticalSnapPicker(
                        values = months,
                        state = state.monthPickerState,
                        modifier = Modifier.weight(1f),
                        repeated = true,
                        itemHeight = itemHeight,
                        itemContent = monthItemContent
                    )
                }
                if (dateEnabled) {
                    VerticalSnapPicker(
                        values = dates,
                        state = state.datePickerState,
                        modifier = Modifier.weight(1f),
                        repeated = true,
                        itemHeight = itemHeight,
                        itemContent = dateItemContent
                    )
                }
            }
        }
    }
}

private fun calculateLastDate(year: Int, month: Int): Int {
    return when (month) {
        1 -> 31
        2 -> if (isLeafYear(year)) 29 else 28
        3 -> 31
        4 -> 30
        5 -> 31
        6 -> 30
        7 -> 31
        8 -> 31
        9 -> 30
        10 -> 31
        11 -> 30
        12 -> 31
        else -> throw IllegalArgumentException("$month is impossible month")
    }
}

private fun isLeafYear(year: Int): Boolean {
    return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)
}
