package com.example.projectorcasting.casting.listener

import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import android.graphics.Canvas
import com.example.projectorcasting.adapter.QueueListAdapter

/**
 * An implementation of the [androidx.recyclerview.widget.ItemTouchHelper.Callback].
 */
class QueueItemTouchHelperCallback constructor(private val mAdapter: ItemTouchHelperAdapter?) :
    ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        //true for drag up and down
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags: Int = ItemTouchHelper.START
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView, source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (source.itemViewType != target.itemViewType) {
            return false
        }
        mAdapter?.onItemMove(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        mAdapter?.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (viewHolder is QueueListAdapter.QueueItemViewHolder) {
                ViewCompat.setTranslationX(viewHolder.mContainer, dX)
            }
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is QueueListAdapter.ItemTouchHelperViewHolder) {
                viewHolder.onItemSelected()
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is QueueListAdapter.ItemTouchHelperViewHolder) {
            viewHolder.onItemClear()
        }
    }

    /**
     * An interface to listen for a move or dismissal event from an
     * [ItemTouchHelper.Callback].
     */
    open interface ItemTouchHelperAdapter {
        /**
         * Called when an item has been dragged far enough to trigger a move. This is called every
         * time an item is shifted, and **not** at the end of a "drop" event.
         *
         * @param fromPosition Original position of the item before move.
         * @param toPosition   Target position of the item after move.
         */
        fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

        /**
         * Called when an item has been dismissed by a swipe.
         *
         * @param position The position of the swiped item.
         */
        fun onItemDismiss(position: Int)
    }
}