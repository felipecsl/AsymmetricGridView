package com.felipecsl.asymmetricgridview;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

@SuppressWarnings("rawtypes")
public final class AsymmetricGridViewAdapter extends BaseAdapter
    implements AGVBaseAdapter, WrapperListAdapter {
  private final ListAdapter wrappedAdapter;
  private final AdapterImpl adapterImpl;

  class GridDataSetObserver extends DataSetObserver {
    @Override public void onChanged() {
      recalculateItemsPerRow();
    }

    @Override public void onInvalidated() {
      recalculateItemsPerRow();
    }
  }

  public AsymmetricGridViewAdapter(Context context, AsymmetricGridView listView,
      ListAdapter adapter) {
    this.adapterImpl = new AdapterImpl(context, this, listView);
    this.wrappedAdapter = adapter;
    wrappedAdapter.registerDataSetObserver(new GridDataSetObserver());
  }

  @Override public AsymmetricItem getItem(int position) {
    return (AsymmetricItem) wrappedAdapter.getItem(position);
  }

  @Override public AsymmetricViewHolder onCreateAsymmetricViewHolder(
      int position, ViewGroup parent, int viewType) {
    return new AsymmetricViewHolder<>(wrappedAdapter.getView(position, null, parent));
  }

  @Override public void onBindAsymmetricViewHolder(
      AsymmetricViewHolder holder, ViewGroup parent, int position) {
    wrappedAdapter.getView(position, holder.itemView, parent);
  }

  @Override public long getItemId(int position) {
    return wrappedAdapter.getItemId(position);
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    AdapterImpl.ViewHolder viewHolder = adapterImpl.onCreateViewHolder();
    adapterImpl.onBindViewHolder(viewHolder, position, parent);
    return viewHolder.itemView;
  }

  @Override public int getCount() {
    // Returns the row count for ListView display purposes
    return adapterImpl.getRowCount();
  }

  @Override public int getItemViewType(int position) {
    return wrappedAdapter.getItemViewType(position);
  }

  @Override public int getActualItemCount() {
    return wrappedAdapter.getCount();
  }

  @Override public ListAdapter getWrappedAdapter() {
    return wrappedAdapter;
  }

  void recalculateItemsPerRow() {
    adapterImpl.recalculateItemsPerRow();
  }
}