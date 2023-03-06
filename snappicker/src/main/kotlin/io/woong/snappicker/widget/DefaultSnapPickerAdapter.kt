package io.woong.snappicker.widget

import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import io.woong.snappicker.ExperimentalSnapPickerApi

/**
 * Default implementation of the adapter.
 */
@OptIn(ExperimentalSnapPickerApi::class)
internal class DefaultSnapPickerAdapter(
    private val maxItemWidth: Int,
    private val maxItemHeight: Int
) : SnapPickerView.Adapter<Any>() {
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

    override fun bindItemView(itemView: View, value: Any) {
        itemView as AppCompatTextView
        itemView.text = value.toString()
    }
}
