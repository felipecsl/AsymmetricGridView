package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felipecsl.asymmetricgridview.app.model.AsymmetricItem;

import java.util.List;

public class ListAdapter extends AsymmetricGridViewAdapter {

    public ListAdapter(final Context context, final AsymmetricGridView listView, final List<AsymmetricItem> items) {
        super(context, listView, items);
    }

    @Override
    public View getActualView(final int position, final View convertView, final ViewGroup parent) {
        TextView v;

        AsymmetricItem item = getItem(position);

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

        v.setText(String.valueOf(position));

        return v;
    }
}
