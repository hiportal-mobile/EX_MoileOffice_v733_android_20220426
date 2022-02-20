package com.ex.group.folder.dragNdropHelper;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface TouchAdapter {

    boolean onItemMoved(int fromPosition, int toPosition);

    boolean onItemMove(int fromPosition, int toPosition,int state);
    //Called when an item has been dragged for enough to trigger a move.
    // This is called every time
    //An item is shifted and not ate the end of a "drop" event;
    void onItemDismiss(int position);
}
