package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felipecsl.asymmetricgridview.app.model.DemoItem;
import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;

import java.util.List;

public class ListAdapter extends AsymmetricGridViewAdapter<DemoItem> {

    public ListAdapter(final Context context, final AsymmetricGridView listView, final List<DemoItem> items) {
        super(context, listView, items);
    }

    @Override
    public View getActualView(final int position, final View convertView, final ViewGroup parent) {
        TextView v;

        DemoItem item = getItem(position);

        if (convertView == null) {
            v = new TextView(getContext());
            v.setGravity(Gravity.CENTER);
            v.setBackgroundColor(Color.parseColor("#cc0000"));
            v.setTextColor(Color.parseColor("#ffffff"));
        } else
            v = (TextView) convertView;

        v.setLayoutParams(new LinearLayout.LayoutParams(
                getRowWidth(item),
                getRowHeight(item)));

        v.setText(String.valueOf(item.getPosition()));

        return v;
    }
}
