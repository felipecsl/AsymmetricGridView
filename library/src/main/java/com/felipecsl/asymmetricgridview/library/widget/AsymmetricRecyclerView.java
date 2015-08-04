package com.felipecsl.asymmetricgridview.library.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public final class AsymmetricRecyclerView extends RecyclerView {
  public AsymmetricRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
  }
}
