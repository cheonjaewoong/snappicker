package io.woong.snappicker.widget

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

public abstract class SnapPickerAdapter<T> : RecyclerView.Adapter<ItemFrameViewHolder<T>>() {
    /**
     * The actual values of this picker.
     * You should use [getValue] method to get item at the specified position.
     */
    private var values: List<T> = emptyList()

    /**
     * The orientation of this picker.
     *
     * Note: Whenever adapter set to picker view, this orientation must be synced to view's orientation.
     */
    internal var orientation: Int = SnapPickerView.ORIENTATION_VERTICAL

    /**
     * Sets new values of the picker.
     */
    public fun setValues(values: List<T>) {
        this.values = values
    }

    /**
     * Returns current values of the picker.
     */
    public fun getValues(): List<T> = values

    /**
     * Returns item at the specified position. [SnapPickerView] can contain [Int.MAX_VALUE] items
     * for cyclic wheel. It may occurs some mistakes. This method calculates actual item position
     * and returns it.
     */
    private fun getValue(position: Int): T {
        val actualPosition = position % values.size
        return values[actualPosition]
    }

    public final override fun getItemCount(): Int = Int.MAX_VALUE

    public final override fun getItemViewType(position: Int): Int = super.getItemViewType(position)

    public final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFrameViewHolder<T> {
        val context = parent.context
        val displayMetrics = parent.resources.displayMetrics
        return ItemFrameViewHolder.create(
            context = context,
            maxItemWidth = if (orientation == SnapPickerView.ORIENTATION_VERTICAL) {
                ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                getMaxItemWidth(displayMetrics)
            },
            maxItemHeight = if (orientation == SnapPickerView.ORIENTATION_HORIZONTAL) {
                ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                getMaxItemHeight(displayMetrics)
            },
            itemView = createItemView(parent)
        )
    }

    /**
     * Returns the max size of this picker's each item width. Size value should be pixels.
     * The default size is pixel size of 48DP in current display.
     *
     * If picker orientation is vertical, it will be ignored. And width is fixed to match parent.
     *
     * @param displayMetrics Display metrics instance of current display.
     */
    public open fun getMaxItemWidth(displayMetrics: DisplayMetrics): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            SnapPickerView.DEFAULT_ITEM_WIDTH_DP.toFloat(),
            displayMetrics
        ).roundToInt()
    }

    /**
     * Returns the max size of this picker's each item height. Size value should be pixels.
     * The default size is pixel size of 48DP in current display.
     *
     * If picker orientation is horizontal, it will be ignored. And height is fixed to match parent.
     *
     * @param displayMetrics Display metrics instance of current display.
     */
    public open fun getMaxItemHeight(displayMetrics: DisplayMetrics): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            SnapPickerView.DEFAULT_ITEM_HEIGHT_DP.toFloat(),
            displayMetrics
        ).roundToInt()
    }

    /**
     * Creates a new item view associated with the specified position.
     *
     * Item view will be constrained in the max size, calculated by [getMaxItemWidth]
     * and [getMaxItemHeight]. If you want to change max item view size, override
     * [getMaxItemWidth] or [getMaxItemHeight].
     */
    public abstract fun createItemView(parent: ViewGroup): View

    public final override fun onBindViewHolder(holder: ItemFrameViewHolder<T>, position: Int) {
        holder.bind(getValue(position), ::bindItemView)
    }

    /**
     * Binds a item view associated with the specified value.
     */
    public abstract fun bindItemView(itemView: View, value: T)
}
