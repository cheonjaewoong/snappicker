package io.woong.snappicker.widget

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.woong.snappicker.R
import kotlin.math.roundToInt

/**
 * The scrollable picker that allows user to select one item from multiple items.
 */
public class SnapPickerView<T> : FrameLayout {

    private val pickerRecycler: RecyclerView

    public constructor(context: Context) : this(context, null)

    public constructor(
        context: Context,
        attrs: AttributeSet?
    ) : this(context, attrs, 0, 0)

    public constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, 0)

    public constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SnapPickerView, defStyleAttr, defStyleRes)
        val orientation = a.getInt(R.styleable.SnapPickerView_android_orientation, ORIENTATION_VERTICAL)
        val itemWidth = a.getDimensionPixelSize(
            R.styleable.SnapPickerView_itemWidth,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_ITEM_WIDTH_DP.toFloat(),
                resources.displayMetrics
            ).roundToInt()
        )
        val itemHeight = a.getDimensionPixelSize(
            R.styleable.SnapPickerView_itemHeight,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_ITEM_HEIGHT_DP.toFloat(),
                resources.displayMetrics
            ).roundToInt()
        )
        a.recycle()

        pickerRecycler = RecyclerView(context)
        pickerRecycler.layoutManager = LinearLayoutManager(context, orientation, false)
        val adapter = DefaultSnapPickerAdapter<T>(itemWidth, itemHeight)
        adapter.orientation = orientation
        pickerRecycler.adapter = adapter
        LinearSnapHelper().attachToRecyclerView(pickerRecycler)
        addView(pickerRecycler)
    }

    /**
     * Sets a new adapter for this picker view.
     */
    public fun setAdapter(adapter: SnapPickerAdapter<T>) {
        adapter.orientation = getOrientation()
        pickerRecycler.adapter = adapter
    }

    /**
     * Returns the current adapter of this picker view.
     */
    @Suppress("UNCHECKED_CAST")
    public fun getAdapter(): SnapPickerAdapter<T> {
        return pickerRecycler.adapter as SnapPickerAdapter<T>
    }

    /**
     * Sets layout orientation of this picker to given value.
     *
     * @param orientation Orientation value, either [RecyclerView.HORIZONTAL] or [RecyclerView.VERTICAL].
     */
    public fun setOrientation(orientation: Int) {
        (pickerRecycler.layoutManager as LinearLayoutManager).orientation = orientation
    }

    /**
     * Returns the current orientation of this picker.
     *
     * @return Current orientation value, either [RecyclerView.HORIZONTAL] or [RecyclerView.VERTICAL].
     */
    public fun getOrientation(): Int {
        return (pickerRecycler.layoutManager as LinearLayoutManager).orientation
    }

    public companion object {
        internal const val ORIENTATION_HORIZONTAL: Int = RecyclerView.HORIZONTAL
        internal const val ORIENTATION_VERTICAL: Int = RecyclerView.VERTICAL

        internal const val DEFAULT_ITEM_WIDTH_DP: Int = 48
        internal const val DEFAULT_ITEM_HEIGHT_DP: Int = 48
    }

    /**
     * Default implementation of the [SnapPickerAdapter].
     */
    private class DefaultSnapPickerAdapter<T>(
        private val maxItemWidth: Int,
        private val maxItemHeight: Int
    ) : SnapPickerAdapter<T>() {
        override fun getMaxItemWidth(displayMetrics: DisplayMetrics): Int = maxItemWidth
        override fun getMaxItemHeight(displayMetrics: DisplayMetrics): Int = maxItemHeight

        override fun createItemView(parent: ViewGroup): View {
            val textView = AppCompatTextView(parent.context)
            textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            textView.gravity = Gravity.CENTER
            return textView
        }

        override fun bindItemView(itemView: View, value: T) {
            itemView as AppCompatTextView
            itemView.text = value.toString()
        }
    }
}
