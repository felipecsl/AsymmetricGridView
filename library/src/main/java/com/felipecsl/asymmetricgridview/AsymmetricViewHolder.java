package com.felipecsl.asymmetricgridview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class AsymmetricViewHolder<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.ViewHolder {
  final VH wrappedViewHolder;

  public AsymmetricViewHolder(VH wrappedViewHolder) {
    super(wrappedViewHolder.itemView);
    this.wrappedViewHolder = wrappedViewHolder;
  }

  public AsymmetricViewHolder(View view) {
    super(view);
    wrappedViewHolder = null;
  }
}
