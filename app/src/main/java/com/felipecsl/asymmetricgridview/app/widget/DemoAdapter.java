package com.felipecsl.asymmetricgridview.app.widget;

import android.widget.ListAdapter;

import com.felipecsl.asymmetricgridview.app.model.DemoItem;

import java.util.List;

public interface DemoAdapter extends ListAdapter {

  void appendItems(List<DemoItem> newItems);

  void setItems(List<DemoItem> moreItems);
}
