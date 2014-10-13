package com.felipecsl.asymmetricgridview.app.widget;

import android.widget.ListAdapter;

import com.felipecsl.asymmetricgridview.app.model.DemoItem;

import java.util.List;

public interface DemoAdapter extends ListAdapter {

    public void appendItems(List<DemoItem> newItems);

    public void setItems(List<DemoItem> moreItems);
}
