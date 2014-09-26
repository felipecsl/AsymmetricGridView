package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.felipecsl.asymmetricgridview.app.R;
import com.felipecsl.asymmetricgridview.app.model.DemoItem;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;

import java.util.List;

// Sample adapter implementation extending from AsymmetricGridViewAdapter<DemoItem>.
// This is the easiest way to get started.
public class DefaultListAdapter extends AsymmetricGridViewAdapter<DemoItem> {

    public DefaultListAdapter(final Context context, final AsymmetricGridView listView, final List<DemoItem> items) {
        super(context, listView, items);
    }

    @Override
    @SuppressWarnings("deprecation")
    public View getActualView(final int position, final View convertView, final ViewGroup parent) {
        TextView v;

        DemoItem item = getItem(position);

        if (convertView == null) {
            v = new TextView(context);
            v.setGravity(Gravity.CENTER);
            v.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.text_view_background_selector));
            v.setTextColor(Color.parseColor("#ffffff"));
            v.setTextSize(Utils.dpToPx(context, 18));
            v.setId(item.getPosition());
        } else
            v = (TextView) convertView;

        v.setText(String.valueOf(item.getPosition()));

        return v;
    }
}