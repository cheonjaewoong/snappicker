package com.example.snappicker.android.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.woong.snappicker.compose.ExperimentalSnapPickerApi
import io.woong.snappicker.compose.VerticalSnapPicker
import io.woong.snappicker.datetime.TimeFormat
import io.woong.snappicker.datetime.TimePeriod
import io.woong.snappicker.compose.datetime.VerticalDateSnapPicker
import io.woong.snappicker.compose.datetime.VerticalTimeSnapPicker
import io.woong.snappicker.compose.datetime.rememberDateSnapPickerState
import io.woong.snappicker.compose.datetime.rememberTimeSnapPickerState
import io.woong.snappicker.compose.rememberSnapPickerState

public class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSnapPickerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(Modifier.fillMaxSize()) {
                val pickerState = rememberSnapPickerState(5)
                LaunchedEffect(pickerState.currentIndex) {
                    Log.d(TAG, "index=${pickerState.currentIndex}")
                }
                VerticalSnapPicker(
                    values = (0..10).toList(),
                    state = pickerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White),
                    decorationBox = { innerPicker ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x11000000))
                        )
                        innerPicker()
                    },
                    itemContent = {
                        BasicText(
                            text = "$it",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                val datePickerState = rememberDateSnapPickerState()
                LaunchedEffect(datePickerState.isScrollInProgress) {
                    if (!datePickerState.isScrollInProgress) {
                        snapshotFlow { datePickerState }.collect {
                            Log.d(
                                TAG,
                                "year=${datePickerState.currentYear}, " +
                                    "month=${datePickerState.currentMonth}, " +
                                    "date=${datePickerState.currentDate}"
                            )
                        }
                    }
                }
                VerticalDateSnapPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White),
                    state = datePickerState,
                    decorationBox = { innerPickers ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x11000000))
                        )
                        innerPickers()
                    },
                    yearItemContent = { year ->
                        BasicText(
                            text = year.toString(),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    },
                    monthItemContent = { month ->
                        BasicText(
                            text = month.toString(),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    },
                    dateItemContent = { date ->
                        BasicText(
                            text = date.toString(),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                val timePickerState = rememberTimeSnapPickerState()
                LaunchedEffect(timePickerState.isScrollInProgress) {
                    if (!timePickerState.isScrollInProgress) {
                        snapshotFlow { timePickerState }.collect {
                            Log.d(
                                TAG,
                                "hour=${timePickerState.currentHour}, " +
                                    "minute=${timePickerState.currentMinute}, " +
                                    "second=${timePickerState.currentSecond}"
                            )
                        }
                    }
                }
                VerticalTimeSnapPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White),
                    state = timePickerState,
                    timeFormat = TimeFormat.Format12Hour,
                    secondEnabled = false,
                    decorationBox = { innerPickers ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x11000000))
                        )
                        innerPickers()
                    },
                    periodItemContent = { amPm ->
                        BasicText(
                            text = when (amPm) {
                                TimePeriod.Am -> "오전"
                                TimePeriod.Pm -> "오후"
                            },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    },
                    hourItemContent = { hour ->
                        BasicText(
                            text = hour.toString(),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    },
                    minuteItemContent = { minute ->
                        BasicText(
                            text = minute.toString(),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    },
                    secondItemContent = { second ->
                        BasicText(
                            text = second.toString(),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                )
            }
        }
    }

    public companion object {
        private const val TAG: String = "MainActivity"
    }
}
