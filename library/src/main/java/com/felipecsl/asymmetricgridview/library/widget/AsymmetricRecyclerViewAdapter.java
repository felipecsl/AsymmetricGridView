package com.felipecsl.asymmetricgridview.library.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public final class AsymmetricRecyclerViewAdapter<T extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<T> {

  private final Context context;
  private final AsymmetricRecyclerView recyclerView;
  private final RecyclerView.Adapter<T> adapter;

  public AsymmetricRecyclerViewAdapter(Context context, AsymmetricRecyclerView recyclerView,
      RecyclerView.Adapter<T> adapter) {
    this.context = context;
    this.recyclerView = recyclerView;
    this.adapter = adapter;
  }

  @Override public T onCreateViewHolder(ViewGroup parent, int viewType) {
    return null;
  }

  @Override public void onBindViewHolder(T holder, int position) {

  }

  @Override public int getItemCount() {
    return 0;
  }
}
