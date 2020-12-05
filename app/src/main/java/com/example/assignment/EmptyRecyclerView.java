package com.example.assignment;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class EmptyRecyclerView extends RecyclerView {
    private View emptyView;

    private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }

    };

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            if(getAdapter().getItemCount() == 0)
            {
                Log.d("empty", " : yes");
                setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            else{
                setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        if(adapter!= null){
            adapter.registerAdapterDataObserver(observer);
        }
        observer.onChanged();
    }
}
