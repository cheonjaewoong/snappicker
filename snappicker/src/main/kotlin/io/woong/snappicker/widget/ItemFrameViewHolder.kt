package io.woong.snappicker.widget

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

/**
 * View holder for handling picker's item views in same size.
 */
public class ItemFrameViewHolder private constructor(
    frameView: FrameLayout,
    private val pickerItemView: View
) : RecyclerView.ViewHolder(frameView) {

    internal fun <T> bind(value: T, bindItemView: (View, T) -> Unit) {
        bindItemView(pickerItemView, value)
    }

    internal companion object {
        internal fun create(
            context: Context,
            maxItemWidth: Int,
            maxItemHeight: Int,
            itemView: View
        ): ItemFrameViewHolder {
            val frame = FrameLayout(context)
            frame.layoutParams = FrameLayout.LayoutParams(maxItemWidth, maxItemHeight)
            frame.addView(itemView)
            return ItemFrameViewHolder(frame, itemView)
        }
    }
}
