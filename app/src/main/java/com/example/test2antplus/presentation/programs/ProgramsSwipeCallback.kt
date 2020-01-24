package com.example.test2antplus.presentation.programs

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.test2antplus.R

class ProgramsSwipeCallback(private val adapter: ProgramsAdapter) : ItemTouchHelper.Callback() {

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
        when (direction) {
            ItemTouchHelper.RIGHT -> {
                adapter.editItem(viewHolder.adapterPosition)
            }
            ItemTouchHelper.LEFT -> {
                adapter.removeItem(viewHolder.adapterPosition)
            }
        }
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

        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20     // background is behind the rounded corners of itemView

        val iconDelete = itemView.context.resources.getDrawable(R.drawable.ic_delete_white_40)
        val iconEdit = itemView.context.resources.getDrawable(R.drawable.ic_edit_white_40)

        val bgRed = ColorDrawable(itemView.context.resources.getColor(R.color.red_700))
        val bgGreen = ColorDrawable(itemView.context.resources.getColor(R.color.green_700))

        val iconMargin = (itemView.height - iconDelete.intrinsicHeight) / 5
        val iconTop = itemView.top + (itemView.height - iconDelete.intrinsicHeight) / 2
        val iconBottom = iconTop + iconDelete.intrinsicHeight

        val background: ColorDrawable
        background = when {
            (dX > 0) -> {   // edit swipe
                val iconLeft = itemView.left + iconMargin + iconEdit.intrinsicHeight
                val iconRight = itemView.left + iconMargin
                iconEdit.setBounds(iconRight, iconTop, iconLeft, iconBottom)

                bgGreen.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset,
                    itemView.bottom
                )
                bgGreen
            }
            (dX < 0) -> {   // delete swipe
                val iconLeft = itemView.right - iconMargin - iconDelete.intrinsicHeight
                val iconRight = itemView.right - iconMargin
                iconDelete.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                bgRed.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                bgRed
            }
            else -> {
                bgRed.setBounds(0, 0, 0, 0)
                bgRed
            }
        }

        background.draw(c)
        iconDelete.draw(c)
        iconEdit.draw(c)
    }
}