package io.woong.snappicker.widget

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

public abstract class SnapPickerAdapter<T> : RecyclerView.Adapter<ItemFrameViewHolder<T>>() {
    private var values: List<T> = emptyList()

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

    public final override fun getItemCount(): Int = values.size

    public final override fun getItemViewType(position: Int): Int = super.getItemViewType(position)

    public final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFrameViewHolder<T> {
        val context = parent.context
        val displayMetrics = parent.resources.displayMetrics
        return ItemFrameViewHolder.create(
            context,
            getMaxItemWidthOrMatchParent(displayMetrics),
            getMaxItemHeightOrMatchParent(displayMetrics),
            createItemView(parent)
        )
    }

    private fun getMaxItemWidthOrMatchParent(displayMetrics: DisplayMetrics): Int {
        return if (orientation == SnapPickerView.ORIENTATION_VERTICAL) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            getMaxItemWidth(displayMetrics)
        }
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

    private fun getMaxItemHeightOrMatchParent(displayMetrics: DisplayMetrics): Int {
        return if (orientation == SnapPickerView.ORIENTATION_HORIZONTAL) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            getMaxItemHeight(displayMetrics)
        }
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
        holder.bind(values[position], ::bindItemView)
    }

    /**
     * Binds a item view associated with the specified value.
     */
    public abstract fun bindItemView(itemView: View, value: T)
}
