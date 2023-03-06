package io.woong.snappicker.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.LazyListSnapperLayoutInfo
import dev.chrisbanes.snapper.rememberLazyListSnapperLayoutInfo
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import io.woong.snappicker.ExperimentalSnapPickerApi
import kotlin.math.abs

/**
 * The horizontal scrollable picker that allows user to select one item from multiple items.
 *
 * @param values Value list to display in picker.
 * @param modifier The modifier to apply to this composable.
 * @param state The state object to manage this picker's state.
 * @param itemWidth The width size of each item composable's container.
 * @param decorationBox Composable to add decoration around picker, such as indicator or something.
 * The actual picker will be passed to this lambda's parameter, "innerPicker".
 * You must call `innerPicker` to display picker.
 * If it is not called, the picker never visible.
 * @param itemContent The content composable of the single item.
 */
@ExperimentalSnapPickerApi
@Composable
public fun <T> HorizontalSnapPicker(
    values: List<T>,
    modifier: Modifier = Modifier,
    state: SnapPickerState = rememberSnapPickerState(),
    itemWidth: Dp = 48.dp,
    decorationBox: @Composable BoxScope.(innerPicker: @Composable () -> Unit) -> Unit =
        @Composable { innerPicker -> innerPicker() },
    itemContent: @Composable BoxScope.(value: T) -> Unit
) {
    CoreSnapPicker(
        values = values,
        state = state,
        isVertical = false,
        itemSize = DpSize(width = itemWidth, height = 0.dp),
        repeated = true,
        modifier = modifier,
        decorationBox = decorationBox,
        itemContent = itemContent
    )
}

/**
 * The vertical scrollable picker that allows user to select one item from multiple items.
 *
 * @param values Value list to display in picker.
 * @param modifier The modifier to apply to this composable.
 * @param state The state object to manage this picker's state.
 * @param itemHeight The height size of each item composable's container.
 * @param decorationBox Composable to add decoration around picker, such as indicator or something.
 * The actual picker will be passed to this lambda's parameter, "innerPicker".
 * You must call `innerPicker` to display picker.
 * If it is not called, the picker never visible.
 * @param itemContent The content composable of the single item.
 */
@ExperimentalSnapPickerApi
@Composable
public fun <T> VerticalSnapPicker(
    values: List<T>,
    modifier: Modifier = Modifier,
    state: SnapPickerState = rememberSnapPickerState(),
    itemHeight: Dp = 48.dp,
    decorationBox: @Composable BoxScope.(innerPicker: @Composable () -> Unit) -> Unit =
        @Composable { innerPicker -> innerPicker() },
    itemContent: @Composable BoxScope.(value: T) -> Unit
) {
    CoreSnapPicker(
        values = values,
        state = state,
        isVertical = true,
        itemSize = DpSize(width = 0.dp, height = itemHeight),
        repeated = true,
        modifier = modifier,
        decorationBox = decorationBox,
        itemContent = itemContent
    )
}

@ExperimentalSnapPickerApi
@Composable
internal fun <T> VerticalSnapPicker(
    values: List<T>,
    modifier: Modifier = Modifier,
    state: SnapPickerState = rememberSnapPickerState(),
    itemHeight: Dp = 48.dp,
    repeated: Boolean = true,
    decorationBox: @Composable BoxScope.(innerPicker: @Composable () -> Unit) -> Unit =
        @Composable { innerPicker -> innerPicker() },
    itemContent: @Composable BoxScope.(value: T) -> Unit
) {
    CoreSnapPicker(
        values = values,
        state = state,
        isVertical = true,
        itemSize = DpSize(width = 0.dp, height = itemHeight),
        repeated = repeated,
        modifier = modifier,
        decorationBox = decorationBox,
        itemContent = itemContent
    )
}

@OptIn(ExperimentalSnapperApi::class)
@ExperimentalSnapPickerApi
@Composable
private fun <T> CoreSnapPicker(
    values: List<T>,
    state: SnapPickerState,
    isVertical: Boolean,
    itemSize: DpSize,
    repeated: Boolean,
    modifier: Modifier,
    decorationBox: @Composable BoxScope.(innerPicker: @Composable () -> Unit) -> Unit,
    itemContent: @Composable BoxScope.(value: T) -> Unit
) {
    val lazyListState = state.lazyListState
    val snapperLayoutInfo = rememberLazyListSnapperLayoutInfo(lazyListState)

    // Update current index when visible items are changed.
    LaunchedEffect(state) {
        snapshotFlow { state.centralVisibleIndexLayoutInfo?.index }.collect { index ->
            if (index != null) {
                state.currentIndex = if (repeated) index % values.size else index
            }
        }
    }

    if (repeated) {
        // Move far from 0, because repeated list should scrollable both side.
        LaunchedEffect(Unit) {
            state.scrollToItem(calculateRepeatedLazyListMidIndex(
                index = state.currentIndex,
                valuesCount = values.size
            ))
        }

        // Reposition position when scroll is finished.
        LaunchedEffect(state.isScrollInProgress) {
            if (!state.isScrollInProgress) {
                state.scrollToItem(calculateRepeatedLazyListMidIndex(
                    index = state.currentIndex,
                    valuesCount = values.size
                ))
            }
        }
    }

    BoxWithConstraints(modifier) {
        decorationBox {
            if (isVertical) {
                LazyColumn(
                    modifier = Modifier.size(maxWidth, maxHeight),
                    state = lazyListState,
                    contentPadding = PaddingValues(vertical = (maxHeight / 2) - (itemSize.height / 2)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    flingBehavior = rememberSnapperFlingBehavior(lazyListState)
                ) {
                    val itemBoxModifier = Modifier
                        .fillMaxWidth()
                        .height(itemSize.height)
                    if (repeated) {
                        items(count = Int.MAX_VALUE) { index ->
                            Box(
                                modifier = itemBoxModifier.pickerAlpha(
                                    isVertical = false,
                                    index = index,
                                    itemHeight = itemSize.height,
                                    lazyListState = lazyListState,
                                    snapperLayoutInfo = snapperLayoutInfo
                                ),
                                content = { itemContent(values[index % values.size]) }
                            )
                        }
                    } else {
                        items(count = values.size) { index ->
                            Box(
                                modifier = itemBoxModifier.pickerAlpha(
                                    isVertical = false,
                                    index = index,
                                    itemHeight = itemSize.height,
                                    lazyListState = lazyListState,
                                    snapperLayoutInfo = snapperLayoutInfo
                                ),
                                content = { itemContent(values[index]) }
                            )
                        }
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.size(maxWidth, maxHeight),
                    state = lazyListState,
                    contentPadding = PaddingValues(horizontal = (maxWidth / 2) - (itemSize.width) / 2),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    flingBehavior = rememberSnapperFlingBehavior(lazyListState)
                ) {
                    val itemBoxModifier = Modifier
                        .width(itemSize.width)
                        .fillMaxHeight()
                    if (repeated) {
                        items(count = Int.MAX_VALUE) { index ->
                            Box(
                                modifier = itemBoxModifier.pickerAlpha(
                                    isVertical = false,
                                    index = index,
                                    itemHeight = itemSize.width,
                                    lazyListState = lazyListState,
                                    snapperLayoutInfo = snapperLayoutInfo
                                ),
                                content = { itemContent(values[index % values.size]) }
                            )
                        }
                    } else {
                        items(count = values.size) { index ->
                            Box(
                                modifier = itemBoxModifier.pickerAlpha(
                                    isVertical = false,
                                    index = index,
                                    itemHeight = itemSize.width,
                                    lazyListState = lazyListState,
                                    snapperLayoutInfo = snapperLayoutInfo
                                ),
                                content = { itemContent(values[index]) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// TODO improve calculating logics
internal fun calculateRepeatedLazyListMidIndex(index: Int, valuesCount: Int): Int {
    return valuesCount * 1000 + index
}

// TODO expose alpha effect option
@OptIn(ExperimentalSnapperApi::class)
@Stable
private fun Modifier.pickerAlpha(
    isVertical: Boolean,
    index: Int,
    itemHeight: Dp,
    lazyListState: LazyListState,
    snapperLayoutInfo: LazyListSnapperLayoutInfo
): Modifier {
    return this.composed(
        inspectorInfo = {
            debugInspectorInfo {
                name = "pickerAlpha"
                properties["lazyListState"] = lazyListState
                properties["snapperLayoutInfo"] = snapperLayoutInfo
                properties["index"] = index
                properties["isVertical"] = isVertical
            }
        },
        factory = {
            val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }
            val absoluteDistanceToIndexSnap = abs(snapperLayoutInfo.distanceToIndexSnap(index))
            Modifier.alpha(
                alpha = if (absoluteDistanceToIndexSnap < itemHeightPx) {
                    1f - (absoluteDistanceToIndexSnap / itemHeightPx) + 0.25f
                } else {
                    0.25f
                }
            )
        }
    )
}
