package com.ex.group.folder.dragNdropHelper;

import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

//                                                                                  -CREATEED by JSP

public class TouchCallback extends ItemTouchHelper.Callback {
    public static final float ALPHA_FULl = 1.0f;
    private TouchAdapter mAdapter;

    public TouchCallback(TouchAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


    //TODO:TouchHelper에서 터치의 움직임을 그리드뷰 혹은 일반적인 리스트에 따라서 움직임을 달리 가져감
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {//레이아웃메니저가 그리드레이아웃 매니저의 인스턴스일때

            //인스턴스가 그리드레이아웃 매니저일때는 스와이프 효과를 주지 않는다.
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder source, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        if(source.getItemViewType() != target.getItemViewType()){
        }else{

              mAdapter.onItemMoved(source.getAdapterPosition(),target.getAdapterPosition());

        }

        super.onMoved(recyclerView, source, fromPos, target, toPos, x, y);
    }

    //TODO:같은 종류의 뷰에 있다면 true를 리턴하고 mAdapterdml 포지션을 실행 시킨다. ㅎㅎㅎ 완전 천재다 interface 좋다
    //onMove에서 하는 일 인터페이스 Adapter의 메소드를 살리고 있다 CaLLBack으로 바꾸고 있는중 ㅎㅎㅎㅎㅎㅎㅎ 가리고있던 벽이 깨지기 시작함 ㅎㅎㅎ
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        //선택한 View와 바꾸려는 View의 타입이 같은지 확인한다. DRAG & DROP 기능을 설명한다.
        View view = source.itemView;
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

            mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition(), getMovementFlags(recyclerView,source));
            return true;

    }

    //TODO:SWIPE to DELETE의 CallBack 구현중 ㅎㅎㅎ 이번 프로젝트에서는 사용되지 않지만 실습 느낌으로 구현해놓자
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    //TODO:아직은 약간은... SAND BOX>>>>>>연구를 계속 해봐야하겠다.
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // 엄청 간단한 녀석임... 연구 끝 . 그냥 뷰가 그려져있을때 움직이는 만큼 그냥 뷰를 흐릿하게 만들어 버리는 역할을 하는 녀석이다.
            //갑자기 고찰... java를 잘하면 안드로이드는 너무나 쉬운것이다. java 공부를 더 많이 해야할것 같다.
            // 기본기가 너무 부족한 듯 하다.
            final float alpha = ALPHA_FULl - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);

        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    //TODO:Viewholder 인터페이스를 구체화 시키는 구간
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // We only want the active item to change

        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            //액션스테이트가  아니라면,,,,,
            //Let the viewHolder Know that this item is calling moved or dragged.
            TouchViewHolder touchViewHolder =(TouchViewHolder)viewHolder;
            touchViewHolder.onItemSelected();
            touchViewHolder.onItemDropped();
        }else if(actionState ==ItemTouchHelper.ACTION_STATE_IDLE){
            TouchViewHolder touchViewHolder =(TouchViewHolder)viewHolder;

        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    //TODO:viewHolder 인터페이스를 구체화 시키는 구간
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //Tell the viewHolder it's time to restore the idle state;
        TouchViewHolder touchViewHolder =(TouchViewHolder)viewHolder;
        touchViewHolder.onItemClear();
        super.clearView(recyclerView, viewHolder);

    }
}
