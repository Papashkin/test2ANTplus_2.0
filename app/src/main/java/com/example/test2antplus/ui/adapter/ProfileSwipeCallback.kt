package com.example.test2antplus.ui.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.test2antplus.R

enum class ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

class ProfileSwipeCallback(private val adapter: ProfileAdapter): ItemTouchHelper.Callback() {

    private val bgRed: ColorDrawable = ColorDrawable(Color.RED)
    private val bgGreen: ColorDrawable = ColorDrawable(Color.GREEN)
    init {
        var buttonState = ButtonsState.GONE
        var swipeBack = false
        var buttonWidth = 300f
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.removeItem(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val iconDelete = viewHolder.itemView.context.resources.getDrawable(R.drawable.ic_delete_white_24)
        val iconEdit = viewHolder.itemView.context.resources.getDrawable(R.drawable.ic_edit_white_24)

        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20 //so bgRed is behind the rounded corners of itemView

        val iconMargin = (itemView.height - iconDelete.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - iconDelete.intrinsicHeight) / 2
        val iconBottom = iconTop + iconDelete.intrinsicHeight

        when {
            (dX > 0) -> {
                val iconLeft = itemView.left + iconMargin + iconEdit.intrinsicHeight
                val iconRight = itemView.left + iconMargin
                iconEdit.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                bgRed.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
                )
            }
            (dX < 0) -> {
                val iconLeft = itemView.right - iconMargin - iconDelete.intrinsicHeight
                val iconRight = itemView.right - iconMargin
                iconDelete.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                bgRed.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
            }
            else -> {
                bgRed.setBounds(0, 0, 0, 0)
            }
        }

        bgRed.draw(c)
        iconDelete.draw(c)
    }
}