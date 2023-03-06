package io.woong.snappicker.compose

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import io.woong.snappicker.ExperimentalSnapPickerApi

/**
 * Creates and remembers a [SnapPickerState].
 *
 * @param initialIndex Initial selected item index.
 */
@ExperimentalSnapPickerApi
@Composable
@Stable
public fun rememberSnapPickerState(initialIndex: Int = 0): SnapPickerState {
    return rememberSaveable(saver = SnapPickerState.Saver) {
        SnapPickerState(initialIndex = initialIndex)
    }
}

/**
 * A state object of the picker.
 *
 * @param initialIndex Initial selected item index.
 */
@ExperimentalSnapPickerApi
@Stable
public class SnapPickerState(initialIndex: Int = 0) : ScrollableState {

    internal val lazyListState = LazyListState(firstVisibleItemIndex = initialIndex)

    private var _currentIndex: Int by mutableStateOf(initialIndex)

    /**
     * The current selected item index in the picker.
     * It may not updated instantly if scroll is on progress.
     */
    public var currentIndex: Int
        get() = _currentIndex
        internal set(value) {
            if (value != _currentIndex) {
               _currentIndex = value
            }
        }

    /**
     * [LazyListItemInfo] of the closest to center from current visible items.
     * If failed, return `null`.
     */
    internal val centralVisibleIndexLayoutInfo: LazyListItemInfo?
        get() {
            val visibleItemsInfo = lazyListState.layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) return null
            return visibleItemsInfo.find { it.offset == 0 }
        }

    public override val isScrollInProgress: Boolean
        get() = lazyListState.isScrollInProgress

    public override fun dispatchRawDelta(delta: Float): Float =
        lazyListState.dispatchRawDelta(delta)

    public override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ): Unit = lazyListState.scroll(scrollPriority, block)

    /**
     * Move to the given item without animation.
     * It means the scroll position will move to target index instantly.
     *
     * @param index The destination item index. It must be non-negative.
     */
    public suspend fun scrollToItem(index: Int): Unit =
        lazyListState.scrollToItem(index, scrollOffset = 0)

    /**
     * Scroll to the given item with animation (smooth scrolling).
     *
     * @param index The destination item index. It must be non-negative.
     */
    public suspend fun animateScrollToItem(index: Int): Unit =
        lazyListState.animateScrollToItem(index, scrollOffset = 0)

    public companion object {
        /**
         * The default saver for [SnapPickerState].
         */
        public val Saver: Saver<SnapPickerState, Int> = Saver(
            save = { it.currentIndex },
            restore = { SnapPickerState(initialIndex = it)
            }
        )
    }
}
