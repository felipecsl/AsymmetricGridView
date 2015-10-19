package com.felipecsl.asymmetricgridview;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AsymmetricGridView extends ListView implements AsymmetricView {
  protected AdapterView.OnItemClickListener onItemClickListener;
  protected AdapterView.OnItemLongClickListener onItemLongClickListener;
  protected AsymmetricGridViewAdapter gridAdapter;
  private final AsymmetricViewImpl viewImpl;

  public AsymmetricGridView(Context context, AttributeSet attrs) {
    super(context, attrs);

    viewImpl = new AsymmetricViewImpl(context);

    final ViewTreeObserver vto = getViewTreeObserver();
    if (vto != null) {
      vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override public void onGlobalLayout() {
          //noinspection deprecation
          getViewTreeObserver().removeGlobalOnLayoutListener(this);
          viewImpl.determineColumns(getAvailableSpace());
          if (gridAdapter != null) {
            gridAdapter.recalculateItemsPerRow();
          }
        }
      });
    }
  }

  @Override public void setOnItemClickListener(OnItemClickListener listener) {
    onItemClickListener = listener;
  }

  @Override public void fireOnItemClick(int position, View v) {
    if (onItemClickListener != null) {
      onItemClickListener.onItemClick(this, v, position, v.getId());
    }
  }

  @Override public void setOnItemLongClickListener(OnItemLongClickListener listener) {
    onItemLongClickListener = listener;
  }

  @Override public boolean fireOnItemLongClick(int position, View v) {
    return onItemLongClickListener != null && onItemLongClickListener
        .onItemLongClick(this, v, position, v.getId());
  }

  @Override public void setAdapter(@NonNull ListAdapter adapter) {
    if (!(adapter instanceof AsymmetricGridViewAdapter)) {
      throw new UnsupportedOperationException(
          "Adapter must be an instance of AsymmetricGridViewAdapter");
    }

    gridAdapter = (AsymmetricGridViewAdapter) adapter;
    super.setAdapter(adapter);

    gridAdapter.recalculateItemsPerRow();
  }

  public void setRequestedColumnWidth(int width) {
    viewImpl.setRequestedColumnWidth(width);
  }

  public void setRequestedColumnCount(int requestedColumnCount) {
    viewImpl.setRequestedColumnCount(requestedColumnCount);
  }

  public int getRequestedHorizontalSpacing() {
    return viewImpl.getRequestedHorizontalSpacing();
  }

  public void setRequestedHorizontalSpacing(int spacing) {
    viewImpl.setRequestedHorizontalSpacing(spacing);
  }

  public void determineColumns() {
    viewImpl.determineColumns(getAvailableSpace());
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    viewImpl.determineColumns(getAvailableSpace());
  }

  @Override @NonNull
  public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    return viewImpl.onSaveInstanceState(superState);
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof AsymmetricViewImpl.SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }

    AsymmetricViewImpl.SavedState ss = (AsymmetricViewImpl.SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());

    viewImpl.onRestoreInstanceState(ss);

    setSelectionFromTop(20, 0);
  }

  @Override public int getNumColumns() {
    return viewImpl.getNumColumns();
  }

  @Override public int getColumnWidth() {
    return viewImpl.getColumnWidth(getAvailableSpace());
  }

  private int getAvailableSpace() {
    return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
  }

  @Override public boolean isAllowReordering() {
    return viewImpl.isAllowReordering();
  }

  public void setAllowReordering(boolean allowReordering) {
    viewImpl.setAllowReordering(allowReordering);
    if (gridAdapter != null) {
      gridAdapter.recalculateItemsPerRow();
    }
  }

  @Override public boolean isDebugging() {
    return viewImpl.isDebugging();
  }

  public void setDebugging(boolean debugging) {
    viewImpl.setDebugging(debugging);
  }
}
