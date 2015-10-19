package com.felipecsl.asymmetricgridview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public final class AsymmetricRecyclerViewAdapter<T extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<AdapterImpl.ViewHolder> implements AGVBaseAdapter<T> {
  private final AsymmetricRecyclerView recyclerView;
  private final AGVRecyclerViewAdapter<T> wrappedAdapter;
  private final AdapterImpl adapterImpl;

  public AsymmetricRecyclerViewAdapter(Context context, AsymmetricRecyclerView recyclerView,
      AGVRecyclerViewAdapter<T> wrappedAdapter) {
    this.recyclerView = recyclerView;
    this.wrappedAdapter = wrappedAdapter;
    this.adapterImpl = new AdapterImpl(context, this, recyclerView);
  }

  @Override public AdapterImpl.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return adapterImpl.onCreateViewHolder();
  }

  @Override public void onBindViewHolder(AdapterImpl.ViewHolder holder, int position) {
    adapterImpl.onBindViewHolder(holder, position, recyclerView);
  }

  @Override public int getItemCount() {
    return wrappedAdapter.getItemCount();
  }

  @Override public AsymmetricItem getItem(int position) {
    return wrappedAdapter.getItem(position);
  }

  @Override public AsymmetricViewHolder<T> onCreateAsymmetricViewHolder(
      int position, ViewGroup parent, int viewType) {
    return new AsymmetricViewHolder<>(wrappedAdapter.onCreateViewHolder(parent, viewType));
  }

  @Override public void onBindAsymmetricViewHolder(AsymmetricViewHolder<T> holder, int position) {
    wrappedAdapter.onBindViewHolder(holder.wrappedViewHolder, position);
  }

  @Override public View getView(int actualIndex, View view, ViewGroup parent) {
    throw new IllegalStateException("Method implemented for this adapter type");
  }

  void recalculateItemsPerRow() {
    adapterImpl.recalculateItemsPerRow();
  }
}
