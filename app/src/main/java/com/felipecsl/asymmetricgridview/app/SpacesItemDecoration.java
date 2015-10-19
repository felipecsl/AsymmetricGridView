package com.felipecsl.asymmetricgridview.app;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
  private final int padding;

  public SpacesItemDecoration(int padding) {
    this.padding = padding;
  }

  @Override public void getItemOffsets(
      Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    outRect.bottom = padding;
  }
}