package io.woong.snappicker.widget

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.woong.snappicker.ExperimentalSnapPickerApi
import io.woong.snappicker.R
import kotlin.math.roundToInt

/**
 * The scrollable picker that allows user to select one item from multiple items.
 */
@ExperimentalSnapPickerApi
public class SnapPickerView : FrameLayout {
    /**
     * Internal recycler view to display values in a list.
     */
    private val pickerRecycler: RecyclerView

    /**
     * The orientation of this picker. Either [ORIENTATION_HORIZONTAL] or [ORIENTATION_VERTICAL].
     */
    @RecyclerView.Orientation
    public var orientation: Int
        get() = (pickerRecycler.layoutManager as LinearLayoutManager).orientation
        set(value) {
            (pickerRecycler.layoutManager as LinearLayoutManager).orientation = value
        }

    /**
     * The option determines whether this picker displays values cyclical.
     */
    public var isCyclic: Boolean = true

    private var onScrollListener: OnScrollListener? = null
    // TODO: make to work
    private var onValueChangedListener: OnValueChangedListener<*>? = null

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
        // Obtain options from XML.
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
        isCyclic = a.getBoolean(R.styleable.SnapPickerView_cyclic, true)
        a.recycle()

        // Initialize view.
        pickerRecycler = RecyclerView(context)
        pickerRecycler.layoutManager = LinearLayoutManager(context, orientation, false)
        setAdapter(DefaultSnapPickerAdapter(itemWidth, itemHeight))
        LinearSnapHelper().attachToRecyclerView(pickerRecycler)
        pickerRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                onScrollListener?.onScrollStateChanged(this@SnapPickerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollListener?.onScrolled(this@SnapPickerView, dx, dy)
            }
        })
        addView(pickerRecycler)
    }

    protected override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val mainAxisViewSize: Int
        val mainAxisItemSize: Int
        if (orientation == ORIENTATION_HORIZONTAL) {
            mainAxisViewSize = measuredWidth
            mainAxisItemSize = getAdapter<Any>()?.getMaxItemWidth(resources.displayMetrics) ?: 0
        } else {
            mainAxisViewSize = measuredHeight
            mainAxisItemSize = getAdapter<Any>()?.getMaxItemHeight(resources.displayMetrics) ?: 0
        }
        val recyclerPadding = (mainAxisViewSize / 2) - (mainAxisItemSize / 2)
        if (orientation == ORIENTATION_HORIZONTAL) {
            pickerRecycler.setPadding(recyclerPadding, 0, recyclerPadding, 0)
        } else {
            pickerRecycler.setPadding(0, recyclerPadding, 0, recyclerPadding)
        }
        pickerRecycler.clipToPadding = false
        super.onLayout(changed, left, top, right, bottom)
    }

    /**
     * Sets a new adapter for this picker view.
     */
    public fun setAdapter(adapter: Adapter<*>) {
        getAdapter<Any>()?.detachFromPickerView()
        pickerRecycler.adapter = adapter
        adapter.attachToPickerView(this)
    }

    /**
     * Returns the current adapter of this picker view.
     * If there is no adapter, it returns `null`.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T> getAdapter(): Adapter<T>? {
        return pickerRecycler.adapter as Adapter<T>?
    }

    /**
     * Sets new values list to this picker.
     * This is convenience method to set values into internal adapter.
     */
    public fun <T> setValues(values: List<T>) {
        val adapter = getAdapter<T>()
            ?: throw IllegalStateException("Adapter must be set before set values")
        adapter.setValues(values)
    }

    /**
     * Gets a value at the specified position.
     * This is convenience method to get value from internal adapter.
     *
     * @param position The position in this picker.
     */
    public fun <T> getValue(position: Int): T {
        return getAdapter<T>()?.getValue(position)
            ?: throw IllegalStateException("Adapter must be set before get value")
    }

    /**
     * Sets a scroll listener to receive scroll changing events.
     */
    public fun setOnScrollListener(onScrollListener: OnScrollListener?) {
        this.onScrollListener = onScrollListener
    }

    public companion object {
        public const val ORIENTATION_HORIZONTAL: Int = RecyclerView.HORIZONTAL
        public const val ORIENTATION_VERTICAL: Int = RecyclerView.VERTICAL

        internal const val DEFAULT_ITEM_WIDTH_DP: Int = 48
        internal const val DEFAULT_ITEM_HEIGHT_DP: Int = 48
    }

    /**
     * Adapter class for binding data to view that is displayed in the picker.
     */
    public abstract class Adapter<T> : RecyclerView.Adapter<ItemFrameViewHolder>() {
        /**
         * The value list to display in this picker.
         */
        private var values: List<T> = emptyList()

        /**
         * Picker view reference to sync some options to this adapter.
         */
        private var pickerView: SnapPickerView? = null

        private val orientation: Int
            get() = requireNotNull(pickerView).orientation

        private val isCyclic: Boolean
            get() = requireNotNull(pickerView).isCyclic

        internal fun attachToPickerView(pickerView: SnapPickerView) {
            this.pickerView = pickerView
        }

        internal fun detachFromPickerView() {
            if (pickerView != null) {
                pickerView = null
            }
        }

        /**
         * Sets new values list to this picker.
         */
        public fun setValues(values: List<T>) {
            this.values = values
        }

        /**
         * Gets a value at the specified position.
         *
         * @param position The position in this picker.
         */
        public fun getValue(position: Int): T {
            return if (isCyclic) {
                values[position % values.size]
            } else {
                values[position]
            }
        }

        public final override fun getItemCount(): Int = if (isCyclic) Int.MAX_VALUE else values.size

        public final override fun getItemViewType(position: Int): Int = super.getItemViewType(position)

        public final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFrameViewHolder {
            val context = parent.context
            val displayMetrics = parent.resources.displayMetrics
            return ItemFrameViewHolder.create(
                context = context,
                maxItemWidth = if (orientation == ORIENTATION_VERTICAL) {
                    ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    getMaxItemWidth(displayMetrics)
                },
                maxItemHeight = if (orientation == ORIENTATION_HORIZONTAL) {
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
                DEFAULT_ITEM_WIDTH_DP.toFloat(),
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
                DEFAULT_ITEM_HEIGHT_DP.toFloat(),
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

        public final override fun onBindViewHolder(holder: ItemFrameViewHolder, position: Int) {
            holder.bind(getValue(position), ::bindItemView)
        }

        /**
         * Binds a item view associated with the specified value.
         */
        public abstract fun bindItemView(itemView: View, value: T)
    }

    /**
     * Scroll listener to receive picker scrolling events.
     */
    public abstract class OnScrollListener {
        /**
         * Callback that invoked when the picker's scroll state changed.
         *
         * @param pickerView The scrolled [SnapPickerView].
         * @param state The updated scroll state.
         */
        public open fun onScrollStateChanged(pickerView: SnapPickerView, state: Int) {}

        /**
         * Callback that invoked when the picker has been scrolled.
         *
         * @param pickerView The scrolled [SnapPickerView].
         * @param dx Delta of horizontal scroll.
         * @param dy Delta of vertical scroll.
         */
        public open fun onScrolled(pickerView: SnapPickerView, dx: Int, dy: Int) {}
    }

    /**
     * A listener to receive picker's selected value changed event.
     */
    public fun interface OnValueChangedListener<T> {
        /**
         * Callback that invoked when the picker's current selected value has been changed.
         *
         * @param value The new selected value.
         */
        public fun onValueChanged(value: T)
    }
}
