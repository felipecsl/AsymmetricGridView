package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.felipecsl.asymmetricgridview.app.R;
import com.felipecsl.asymmetricgridview.app.model.DemoItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Sample adapter implementation extending from AsymmetricGridViewAdapter<DemoItem>
 * This is the easiest way to get started.
 */
public class DefaultListAdapter extends ArrayAdapter<DemoItem> implements DemoAdapter {

  private final LayoutInflater layoutInflater;

  public DefaultListAdapter(Context context, List<DemoItem> items) {
    super(context, 0, items);
    layoutInflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
  }

  @Override
  @SuppressWarnings("deprecation")
  public View getView(int position, View convertView, @NotNull ViewGroup parent) {
    TextView v;

    DemoItem item = getItem(position);

    if (convertView == null) {
      v = (TextView) layoutInflater.inflate(
          R.layout.adapter_item, parent, false);
    } else {
      v = (TextView) convertView;
    }

    v.setText(String.valueOf(item.getPosition()));

    return v;
  }

  public void appendItems(List<DemoItem> newItems) {
    addAll(newItems);
    notifyDataSetChanged();
  }

  public void setItems(List<DemoItem> moreItems) {
    clear();
    appendItems(moreItems);
  }
}