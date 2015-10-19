package com.felipecsl.asymmetricgridview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

interface AGVBaseAdapter<VH extends RecyclerView.ViewHolder> {
  int getItemCount();
  AsymmetricItem getItem(int position);
  void notifyDataSetChanged();
  int getItemViewType(int actualIndex);
  AsymmetricViewHolder<VH> onCreateAsymmetricViewHolder(int position, ViewGroup parent, int viewType);
  void onBindAsymmetricViewHolder(AsymmetricViewHolder<VH> holder, int position);
  View getView(int actualIndex, View view, ViewGroup parent);
}
