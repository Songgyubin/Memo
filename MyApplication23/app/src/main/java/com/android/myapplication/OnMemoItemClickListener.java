package com.android.myapplication;

import android.view.View;

public interface OnMemoItemClickListener {

    public void onItemClick(MemoAdapter.ViewHolder holder, View view, int position);
    public void onItemLongClick(MemoAdapter.ViewHolder holder, View view, int position);
//    public void onItemClick(MemoAdapter.ViewHolder holder, View view, int position);

}
